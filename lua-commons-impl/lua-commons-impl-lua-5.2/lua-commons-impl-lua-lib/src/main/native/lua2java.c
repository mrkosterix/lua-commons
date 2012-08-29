#include "stdio.h"
#include "stdlib.h"
#include "string.h"
#include "math.h"
#include "lua2java.h"
#include "lua.h"
#include "lualib.h"
#include "lauxlib.h"

#define USERDATA_TYPE       "__type"
#define USERDATA_TYPE_JOBJECT    "JOBJECT"
#define USERDATA_TYPE_UNDEFINED    "UNDEFINED"
#define USERDATA_CLASSNAME       "__CLASSNAME"

#define METATABLE_HIDDEN  "__HIDDEN"

struct JOBJECT
{
   jobject ref;
};

JavaVM* vm = NULL;

typedef struct JOBJECT JOBJECT;

static JOBJECT* toobject(lua_State* L, int index)
{
   return (JOBJECT*)lua_touserdata(L, index);
}

static int objectdestructor(lua_State* L)
{
   JOBJECT* userdata = toobject(L, -1);
   jobject ref = userdata->ref;
   JNIEnv* env;
   int attached = (*vm)->AttachCurrentThread(vm, (void **) &env, NULL);
   (*env)->DeleteGlobalRef(env, ref);
   if (attached)
      (*vm)->DetachCurrentThread(vm);
   return 0;
}

static int proxy(lua_State* L)
{
   if (!lua_getmetatable(L, lua_upvalueindex(1)))
   {
      lua_pushstring(L, "Upvalue with java function object not found.");
      return lua_error(L);
   }
   lua_getfield(L, -1, lua_tostring(L, lua_upvalueindex(2)));
   lua_insert(L, 1);
   lua_pop(L, 1);
   lua_call(L, lua_gettop(L) - 1, -1);
   return lua_gettop(L);
}

static void setproxy(lua_State* L, const char* method)
{
   lua_pushstring(L, method);
   lua_pushvalue(L, -2);
   lua_pushstring(L, method);
   lua_pushcclosure(L, &proxy, 2);
   lua_rawset(L, -3);
}

static void pushobject(JNIEnv* env, lua_State* L, jobject value)
{
   jobject globalRef = (*env)->NewGlobalRef(env, value);

   JOBJECT* userdata = lua_newuserdata(L, sizeof(JOBJECT));
   userdata->ref = globalRef;

   // add destructor metatable
   lua_newtable(L);
   lua_pushstring(L, "__gc");
   lua_pushcfunction(L, &objectdestructor);
   lua_rawset(L, -3);
   setproxy(L, "__add");
   setproxy(L, "__sub");
   setproxy(L, "__mul");
   setproxy(L, "__div");
   setproxy(L, "__mod");
   setproxy(L, "__pow");
   setproxy(L, "__unm");
   setproxy(L, "__concat");
   setproxy(L, "__len");
   setproxy(L, "__eq");
   setproxy(L, "__lt");
   setproxy(L, "__le");
   setproxy(L, "__index");
   setproxy(L, "__newindex");
   setproxy(L, "__call");
   lua_pushstring(L, METATABLE_HIDDEN);
   lua_pushboolean(L, 1);
   lua_rawset(L, -3);
   lua_setmetatable(L, -2);

   jclass class =(*env)->GetObjectClass(env, value);
   jmethodID getnameID = (*env)->GetMethodID(env, (*env)->FindClass(env, "java/lang/Class"), "getName", "()Ljava/lang/String;");
   jstring jclassname = (jstring)(*env)->CallObjectMethod(env, class, getnameID, NULL);
   const char* classname = (*env)->GetStringUTFChars(env, jclassname, NULL);

   lua_newtable(L);
   lua_pushstring(L, USERDATA_TYPE);
   lua_pushstring(L, USERDATA_TYPE_JOBJECT);
   lua_rawset(L, -3);
   lua_pushstring(L, USERDATA_CLASSNAME);
   lua_pushstring(L, classname);
   lua_rawset(L, -3);
   lua_setuservalue(L, -2);

   (*env)->ReleaseStringUTFChars(env, jclassname, classname);
}

const char* JOBJECT_STR = "java.lang.Object";
static int isinstanceof(JNIEnv* env, const char* name, const char* expected)
{
   jmethodID getnameID = (*env)->GetMethodID(env, (*env)->FindClass(env, "java/lang/Class"), "getName", "()Ljava/lang/String;");

   char* cn = malloc(strlen(name) + 1);
   strcpy(cn, name);
   int i = 0;
   for (i = 0; cn[i] != 0; i++)
      if (cn[i] == '.')
         cn[i] = '/';
   jclass class = (*env)->FindClass(env, cn);
   free(cn);
   while (strcmp(name, JOBJECT_STR) != 0)
   {
      if (strcmp(name, expected) == 0)
         return 1;
      class = (*env)->GetSuperclass(env, class);
      jstring jclassname = (jstring)(*env)->CallObjectMethod(env, class, getnameID, NULL);
      name = (*env)->GetStringUTFChars(env, jclassname, NULL);
   }
   return strcmp(expected, JOBJECT_STR) == 0;
}

static int isobjectx(JNIEnv* env, lua_State* L, int index, const char* expected)
{
   if (!lua_isuserdata(L, index))
      return 0;
   lua_getuservalue(L, index);
   if (!lua_istable(L, -1))
   {
      lua_pop(L, 1);
      return 0;
   }
   lua_pushstring(L, USERDATA_TYPE);
   lua_gettable(L, -2);
   if (!lua_isstring(L, -1))
   {
      lua_pop(L, 2);
      return 0;
   }
   const char* type = lua_tostring(L, -1);
   if (strcmp(USERDATA_TYPE_JOBJECT, type) != 0)
   {
      lua_pop(L, 2);
      return 0;
   }
   lua_pop(L, 1);
   if (expected != NULL)
   {
      lua_pushstring(L, USERDATA_CLASSNAME);
      lua_gettable(L, -2);
      if (!lua_isstring(L, -1))
      {
         lua_pop(L, 2);
         return 0;
      }
      const char* name = lua_tostring(L, -1);
      int result = isinstanceof(env, name, expected);
      lua_pop(L, 2);
      return result;
   }
   lua_pop(L, 1);
   return 1;
}

struct JFUNCTION
{
   jobject owner;
   jmethodID method;
};

typedef struct JFUNCTION JFUNCTION;

static int contextcaller(lua_State* L)
{
   int ctx = 0;
   if (lua_getctx(L, &ctx) == LUA_YIELD)
   {
      lua_rawgeti(L, LUA_REGISTRYINDEX, ctx);
      luaL_unref(L, LUA_REGISTRYINDEX, ctx);
      lua_insert(L, 1);
      lua_call(L, lua_gettop(L) - 1, LUA_MULTRET);
      return lua_gettop(L);
   }
   return 0;
}

static int functioncaller(lua_State* L)
{
   JFUNCTION* function = (JFUNCTION*)lua_touserdata(L, lua_upvalueindex(1));

   JNIEnv* env;
   int attached = (*vm)->AttachCurrentThread(vm, (void **) &env, NULL);

   int i = 2;
   while (lua_isnone(L, lua_upvalueindex(i)) == 0)
   {
      lua_pushvalue(L, lua_upvalueindex(i));
      lua_insert(L, i - 1);
      i++;
   }
   int result = (int)(*env)->CallObjectMethod(env, function->owner, function->method, (long)L);

   jthrowable ex = (*env)->ExceptionOccurred(env);
   if (ex != NULL) {
      jclass exClass = (*env)->FindClass(env, "org/lua/commons/impl/nativelua/NativeYieldRuntimeException");
      if ((*env)->IsInstanceOf(env, ex, exClass))
      {
         (*env)->ExceptionClear(env);
         jboolean isK = (jboolean)(*env)->CallObjectMethod(env, ex, (*env)->GetMethodID(env, exClass, "isK", "()Z"), NULL);
         if (!isK)
         {
            int nResults = (int)(*env)->CallObjectMethod(env, ex, (*env)->GetMethodID(env, exClass, "getN", "()I"), NULL);
            return lua_yield(L, nResults);
         }
         else
         {
            int nResults = (int)(*env)->CallObjectMethod(env, ex, (*env)->GetMethodID(env, exClass, "getN", "()I"), NULL);
            int ctx = (int)(*env)->CallObjectMethod(env, ex, (*env)->GetMethodID(env, exClass, "getCtx", "()I"), NULL);
            return lua_yieldk(L, nResults, ctx, &contextcaller);
         }
      }
   }
   if (attached)
      (*vm)->DetachCurrentThread(vm);
   return result;
}

static int functiondestructor(lua_State* L)
{
   JFUNCTION* function = lua_newuserdata(L, sizeof(JFUNCTION));
   JNIEnv* env;
   int attached = (*vm)->AttachCurrentThread(vm, (void **) &env, NULL);

   (*env)->DeleteGlobalRef(env, function->owner);
   if (attached)
      (*vm)->DetachCurrentThread(vm);
   return 0;
}

static void pushjclosure0(JNIEnv* env, lua_State* L, jobject owner, jmethodID method, int n)
{
   JFUNCTION* function = lua_newuserdata(L, sizeof(JFUNCTION));
   function->owner = (*env)->NewGlobalRef(env, owner);
   function->method = method;
   lua_insert(L, -n - 1);
   lua_pushcclosure(L, &functioncaller, n + 1);

   // add destructor metatable
   lua_newtable(L);
   lua_pushstring(L, "__gc");
   lua_pushcfunction(L, &functiondestructor);
   lua_rawset(L, -3);
   lua_pushstring(L, METATABLE_HIDDEN);
   lua_pushboolean(L, 1);
   lua_rawset(L, -3);
   lua_setmetatable(L, -2);
}

static void pushjclosure(JNIEnv* env, lua_State* L, jobject owner, jstring methodName, int n)
{
   jclass class = (*env)->GetObjectClass(env, owner);

   const char* methodNameStr = (*env)->GetStringUTFChars(env, methodName, NULL);
   jmethodID method = (*env)->GetMethodID(env, class, methodNameStr, "(J)I");
   (*env)->ReleaseStringUTFChars(env, methodName, methodNameStr);

   pushjclosure0(env, L, owner, method, n);
}


// DUMP

char* tmpfilename;
FILE* tmpf;
int buflen;

static void startDump() {
   tmpfilename = tmpnam(NULL);
   tmpf = fopen(tmpfilename, "wb");
   buflen = 0;
}

static void* finishDump() {
   fclose(tmpf);
   tmpf = fopen(tmpfilename, "rb");
   void* buf = malloc(buflen + 1);
   fread(buf, buflen, 1, tmpf);
   fclose(tmpf);
   remove(tmpfilename);
   return buf;
}

static int writer(lua_State * L, const void * buf, size_t size, void * unused)
{
    fwrite(buf, 1, size, tmpf);
    buflen += size;
    return 0;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _getenvironment
 * Signature: ()Lorg/lua/commons/impl/nativelua/NativeLuaEnvironment;
 */
JNIEXPORT jobject JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1getenvironment
  (JNIEnv * env, jobject jobj)
{
   jobject environment;
   jclass tempClass;
   tempClass = ( *env )->FindClass( env , "org/lua/commons/impl/nativelua/NativeLuaEnvironment" );
   environment = ( *env )->AllocObject( env , tempClass );
   if ( environment )
   {
      ( *env )->SetIntField( env , environment , ( *env )->GetFieldID( env , tempClass ,
                                                        "LUAI_MAXSTACK" , "I" ), ( jint ) LUAI_MAXSTACK );
      ( *env )->SetIntField( env , environment , ( *env )->GetFieldID( env , tempClass ,
                                                        "LUA_REGISTRYINDEX" , "I" ), ( jint ) LUA_REGISTRYINDEX );
      ( *env )->SetIntField( env , environment , ( *env )->GetFieldID( env , tempClass ,
                                                        "LUA_RIDX_GLOBALS" , "I" ), ( jint ) LUA_RIDX_GLOBALS );
   }
   return environment;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isnil
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isnil
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_isnil(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isnone
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isnone
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_isnone(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isnoneornil
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isnoneornil
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_isnoneornil(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isboolean
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isboolean
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_isboolean(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isnumber
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isnumber
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_isnumber(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isintegerx
 * Signature: (JIII)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isintegerx
  (JNIEnv * env, jobject jobj, jlong cptr, jint index, jint minvalue, jint maxvalue)
{
   lua_State * L = (lua_State*) cptr;
   int res;
   double curr = lua_tonumberx(L, index, &res);
   if (!res) return 0;
   long intval = round(curr);
   return fabs(curr - intval) < 0.000000001 && intval >= minvalue && intval <= maxvalue;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isstring
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isstring
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_isstring(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isfunction
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isfunction
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_isfunction(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _istable
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1istable
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_istable(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isthread
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isthread
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_isthread(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isuserdata
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isuserdata
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_isuserdata(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isjfunction
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isjfunction
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_iscfunction(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _isobjectx
 * Signature: (JILjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1isobjectx
  (JNIEnv * env, jobject jobj, jlong cptr, jint index, jstring className)
{
   lua_State * L = (lua_State*) cptr;
   if (className == NULL)
      return isobjectx(env, L, index, NULL);
   else
   {
      const char* expectedName = (*env)->GetStringUTFChars(env, className, NULL);
      int result = isobjectx(env, L, index, expectedName);
      (*env)->ReleaseStringUTFChars(env, className, expectedName);
      return result;
   }
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pushnil
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pushnil
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   lua_pushnil(L);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pushboolean
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pushboolean
  (JNIEnv * env, jobject jobj, jlong cptr, jboolean value)
{
   lua_State * L = (lua_State*) cptr;
   lua_pushboolean(L, value);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pushinteger
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pushinteger
  (JNIEnv * env, jobject jobj, jlong cptr, jint value)
{
   lua_State * L = (lua_State*) cptr;
   lua_pushinteger(L, value);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pushnumber
 * Signature: (JD)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pushnumber
  (JNIEnv * env, jobject jobj, jlong cptr, jdouble value)
{
   lua_State * L = (lua_State*) cptr;
   lua_pushnumber(L, value);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pushstring
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pushstring
  (JNIEnv * env, jobject jobj, jlong cptr, jstring value)
{
   lua_State * L = (lua_State*) cptr;
   const char* uniStr = (*env)->GetStringUTFChars(env, value, NULL);
   lua_pushstring( L , uniStr );
   (*env)->ReleaseStringUTFChars(env, value, uniStr);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pushobject
 * Signature: (JLjava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pushobject
  (JNIEnv * env, jobject jobj, jlong cptr, jobject value)
{
   lua_State * L = (lua_State*) cptr;
   pushobject(env, L, value);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pushjclosure
 * Signature: (JLjava/lang/Object;Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pushjclosure
  (JNIEnv * env, jobject jobj, jlong cptr, jobject owner, jstring methodName, jint n)
{
   lua_State * L = (lua_State*) cptr;
   pushjclosure(env, L, owner, methodName, n);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pushthread
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pushthread
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   return (jboolean)lua_pushthread(L);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _toboolean
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1toboolean
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_toboolean(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _tointeger
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1tointeger
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_tointeger(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _tonumber
 * Signature: (JI)D
 */
JNIEXPORT jdouble JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1tonumber
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_tonumber(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _tostring
 * Signature: (JI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1tostring
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   const char * str = lua_tostring(L, index);
   return (*env)->NewStringUTF(env, str);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _toobject
 * Signature: (JI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1toobject
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   JOBJECT* object = toobject(L, index);
   return object->ref;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _tothread
 * Signature: (JI)J
 */
JNIEXPORT jlong JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1tothread
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return (long)lua_tothread(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pop
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pop
  (JNIEnv * env, jobject jobj, jlong cptr, jint n)
{
   lua_State * L = (lua_State*) cptr;
   lua_pop(L, n);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pushvalue
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pushvalue
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_pushvalue(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _gettop
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1gettop
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   return lua_gettop(L);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _settop
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1settop
  (JNIEnv * env, jobject jobj, jlong cptr, jint n)
{
   lua_State * L = (lua_State*) cptr;
   lua_settop(L, n);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _copy
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1copy
  (JNIEnv * env, jobject jobj, jlong cptr, jint fromidx, jint toidx)
{
   lua_State * L = (lua_State*) cptr;
   lua_copy(L, fromidx, toidx);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _insert
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1insert
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_insert(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _remove
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1remove
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_remove(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _replace
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1replace
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_replace(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _type
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1type
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_type(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _typename
 * Signature: (JI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1typename
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   const char * str = lua_typename(L, index);
   return (*env)->NewStringUTF(env, str);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _checkstack
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1checkstack
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_checkstack(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _absindex
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1absindex
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_absindex(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _arith
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1arith
  (JNIEnv * env, jobject jobj, jlong cptr, jint op)
{
   lua_State * L = (lua_State*) cptr;
   return lua_arith(L, op);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _compare
 * Signature: (JIII)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1compare
  (JNIEnv * env, jobject jobj, jlong cptr, jint index1, jint index2, jint op)
{
   lua_State * L = (lua_State*) cptr;
   return lua_compare(L, index1, index2, op);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _concat
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1concat
  (JNIEnv * env, jobject jobj, jlong cptr, jint n)
{
   lua_State * L = (lua_State*) cptr;
   lua_concat(L, n);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Lnewstate
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Lnewstate
  (JNIEnv * env, jobject jobj)
{
   if (vm == NULL)
      (*env)->GetJavaVM(env, &vm);
   lua_State * L = luaL_newstate();
   return (jlong) L;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Ldofile
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Ldofile
  (JNIEnv * env, jobject jobj, jlong cptr, jstring filename)
{
   lua_State * L = (lua_State*) cptr;
   const char * uniStr;
   uniStr = (*env)->GetStringUTFChars(env, filename, NULL);
   int result = luaL_dofile(L, uniStr);
   (*env)->ReleaseStringUTFChars(env, filename, uniStr);
   return result;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Ldostring
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Ldostring
  (JNIEnv * env, jobject jobj, jlong cptr, jstring string)
{
   lua_State * L = (lua_State*) cptr;
   const char * uniStr;
   uniStr = (*env)->GetStringUTFChars(env, string, NULL);
   int result = luaL_dostring(L, uniStr);
   (*env)->ReleaseStringUTFChars(env, string, uniStr);
   return result;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1close
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   lua_close(L);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _newthread
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1newthread
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   return (long)lua_newthread(L);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Lopenlibs0
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Lopenlibs0
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   luaL_openlibs(L);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Lloadfile
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Lloadfile
  (JNIEnv * env, jobject jobj, jlong cptr, jstring filename)
{
   lua_State * L = (lua_State*) cptr;
   const char * uniStr;
   uniStr = (*env)->GetStringUTFChars(env, filename, NULL);
   int result = luaL_loadfile(L, uniStr);
   (*env)->ReleaseStringUTFChars(env, filename, uniStr);
   return result;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Lloadstring
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Lloadstring
  (JNIEnv * env, jobject jobj, jlong cptr, jstring string)
{
   lua_State * L = (lua_State*) cptr;
   const char * uniStr;
   uniStr = (*env)->GetStringUTFChars(env, string, NULL);
   int result = luaL_loadstring(L, uniStr);
   (*env)->ReleaseStringUTFChars(env, string, uniStr);
   return result;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _getglobal
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1getglobal
  (JNIEnv * env, jobject jobj, jlong cptr, jstring name)
{
   lua_State * L = (lua_State*) cptr;
   const char * uniStr;
   uniStr = (*env)->GetStringUTFChars(env, name, NULL);
   lua_getglobal(L, uniStr);
   (*env)->ReleaseStringUTFChars(env, name, uniStr);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _setglobal
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1setglobal
  (JNIEnv * env, jobject jobj, jlong cptr, jstring name)
{
   lua_State * L = (lua_State*) cptr;
   const char * uniStr;
   uniStr = (*env)->GetStringUTFChars(env, name, NULL);
   lua_setglobal(L, uniStr);
   (*env)->ReleaseStringUTFChars(env, name, uniStr);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _createtable
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1createtable
  (JNIEnv * env, jobject jobj, jlong cptr, jint narr, jint nrec)
{
   lua_State * L = (lua_State*) cptr;
   lua_createtable(L, narr, nrec);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _gettable
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1gettable
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_gettable(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _settable
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1settable
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_settable(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _getfield
 * Signature: (JILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1getfield
  (JNIEnv * env, jobject jobj, jlong cptr, jint index, jstring key)
{
   lua_State * L = (lua_State*) cptr;
   const char * uniStr;
   uniStr = (*env)->GetStringUTFChars(env, key, NULL);
   lua_getfield(L, index, uniStr);
   (*env)->ReleaseStringUTFChars(env, key, uniStr);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _setfield
 * Signature: (JILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1setfield
  (JNIEnv * env, jobject jobj, jlong cptr, jint index, jstring key)
{
   lua_State * L = (lua_State*) cptr;
   const char * uniStr;
   uniStr = (*env)->GetStringUTFChars(env, key, NULL);
   lua_setfield(L, index, uniStr);
   (*env)->ReleaseStringUTFChars(env, key, uniStr);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _next
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1next
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return (jboolean)lua_next(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _rawget
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1rawget
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_rawget(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _rawset
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1rawset
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_rawset(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _rawgeti
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1rawgeti
  (JNIEnv * env, jobject jobj, jlong cptr, jint index, jint n)
{
   lua_State * L = (lua_State*) cptr;
   lua_rawgeti(L, index, n);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _rawseti
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1rawseti
  (JNIEnv * env, jobject jobj, jlong cptr, jint index, jint n)
{
   lua_State * L = (lua_State*) cptr;
   lua_rawseti(L, index, n);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Lref
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Lref
  (JNIEnv * env, jobject jobj, jlong cptr, jint t)
{
   lua_State * L = (lua_State*) cptr;
   return luaL_ref(L, t);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Lunref
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Lunref
  (JNIEnv * env, jobject jobj, jlong cptr, jint t, jint ref)
{
   lua_State * L = (lua_State*) cptr;
   luaL_unref(L, t, ref);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _rawequal
 * Signature: (JII)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1rawequal
  (JNIEnv * env, jobject jobj, jlong cptr, jint index1, jint index2)
{
   lua_State * L = (lua_State*) cptr;
   return (jboolean)lua_rawequal(L, index1, index2);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _getmetatable
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1getmetatable
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;

   lua_pushvalue(L, index);
   while (lua_getmetatable(L, -1))
   {
      lua_pushstring(L, METATABLE_HIDDEN);
      lua_rawget(L, -2);
      if (lua_isnil(L, -1))
      {
         lua_pop(L, 1);
         lua_insert(L, -2);
         lua_pop(L, 1);
         return 1;
      }
      lua_pop(L, 1);
      lua_insert(L, -2);
      lua_pop(L, 1);
   }
   lua_pop(L, 1);
   return 0;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _setmetatable
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1setmetatable
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;

   lua_pushvalue(L, index);
   while (lua_getmetatable(L, -1))
   {
      lua_pushstring(L, METATABLE_HIDDEN);
      lua_rawget(L, -2);
      if (lua_isnil(L, -1))
      {
         lua_pop(L, 2);
         break;
      }
      lua_pop(L, 1);
      lua_insert(L, -2);
      lua_pop(L, 1);
   }
   lua_pushvalue(L, -2);
   lua_setmetatable(L, -2);
   lua_pop(L, 2);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _getuservalue
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1getuservalue
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_getuservalue(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _setuservalue
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1setuservalue
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_setuservalue(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _call
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1call
  (JNIEnv * env, jobject jobj, jlong cptr, jint nArgs, jint nRes)
{
   lua_State * L = (lua_State*) cptr;
   lua_call(L, nArgs, nRes);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pcall
 * Signature: (JIII)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pcall
  (JNIEnv * env, jobject jobj, jlong cptr, jint nArgs, jint nRes, jint msgh)
{
   lua_State * L = (lua_State*) cptr;
   return lua_pcall(L, nArgs, nRes, msgh);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _callk
 * Signature: (JIILjava/lang/Object;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1callk
  (JNIEnv * env, jobject jobj, jlong cptr, jint nArgs, jint nRes, jobject owner, jstring methodName)
{
   lua_State * L = (lua_State*) cptr;

   pushjclosure(env, L, owner, methodName, 0);
   int ctx = luaL_ref(L, LUA_REGISTRYINDEX);

   lua_callk(L, nArgs, nRes, ctx, &contextcaller);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _pcallk
 * Signature: (JIIILjava/lang/Object;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1pcallk
  (JNIEnv * env, jobject jobj, jlong cptr, jint nArgs, jint nRes, jint errfunc, jobject owner, jstring methodName)
{
   lua_State * L = (lua_State*) cptr;

   pushjclosure(env, L, owner, methodName, 1);
   int ctx = luaL_ref(L, LUA_REGISTRYINDEX);

   return lua_pcallk(L, nArgs, nRes, errfunc, ctx, &contextcaller);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _yield
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1yield
  (JNIEnv * env, jobject jobj, jlong cptr, jint nResults)
{
   jclass exClass = (*env)->FindClass(env, "org/lua/commons/impl/nativelua/NativeYieldRuntimeException");
   jobject ex = (*env)->NewObject(env, exClass, (*env)->GetMethodID(env, exClass, "<init>", "(I)V"), nResults);

   (*env)->Throw(env, (jthrowable)ex);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _yieldk
 * Signature: (JILjava/lang/Object;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1yieldk
  (JNIEnv * env, jobject jobj, jlong cptr, jint nResults, jobject owner, jstring methodName)
{
   lua_State * L = (lua_State*) cptr;

   pushjclosure(env, L, owner, methodName, 0);
   int ctx = luaL_ref(L, LUA_REGISTRYINDEX);

   jclass exClass = (*env)->FindClass(env, "org/lua/commons/impl/nativelua/NativeYieldRuntimeException");
   jobject ex = (*env)->NewObject(env, exClass, (*env)->GetMethodID(env, exClass, "<init>", "(II)V"), ctx, nResults);

   (*env)->Throw(env, (jthrowable)ex);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _resume
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1resume
  (JNIEnv * env, jobject jobj, jlong cptr, jlong from, jint nArg)
{
   lua_State * L = (lua_State*) cptr;
   return lua_resume(L, (lua_State*)from, nArg);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _getctxstate
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1getctxstate
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   int ctx;
   return lua_getctx(L, &ctx);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _status
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1status
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   return lua_status(L);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _version
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1version
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   return *lua_version(L);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _len
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1len
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   lua_len(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _rawlen
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1rawlen
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   return lua_rawlen(L, index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _error
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1error
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   return lua_error(L);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Ltypename
 * Signature: (JI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Ltypename
  (JNIEnv * env, jobject jobj, jlong cptr, jint index)
{
   lua_State * L = (lua_State*) cptr;
   const char * str = luaL_typename(L, index);
   return (*env)->NewStringUTF(env, str);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Lwhere
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Lwhere
  (JNIEnv * env, jobject jobj, jlong cptr, jint lvl)
{
   lua_State * L = (lua_State*) cptr;
   luaL_where(L, lvl);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _upvalueindex
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1upvalueindex
  (JNIEnv * env, jobject jobj, jint index)
{
   return lua_upvalueindex(index);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _dump
 * Signature: (J)[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1dump
  (JNIEnv * env, jobject jobj, jlong cptr)
{
   lua_State * L = (lua_State*) cptr;
   startDump();
   lua_dump(L, writer, NULL);
   void* dump = finishDump();

   jbyteArray result = (*env)->NewByteArray(env, buflen);
   (*env)->SetByteArrayRegion(env, result, (jint)0, (jint)buflen, (jbyte*)dump);

   return result;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _Lloadbuffer
 * Signature: (J[BLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1Lloadbuffer
  (JNIEnv * env, jobject jobj, jlong cptr, jbyteArray bytes, jstring name)
{
   lua_State * L = (lua_State*) cptr;
   
   const char * nameStr;
   nameStr = (*env)->GetStringUTFChars(env, name, NULL);

   jint buflen = (*env)->GetArrayLength(env, bytes);
   const char* data = malloc(buflen);
   (*env)->GetByteArrayRegion(env, bytes, (jint)0, (jint)buflen, (jbyte*)data);

   int result = luaL_loadbuffer(L, data, buflen, nameStr);

   (*env)->ReleaseStringUTFChars(env, name, nameStr);
   return result;
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _getupvalue
 * Signature: (JII)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1getupvalue
  (JNIEnv * env, jobject jobj, jlong cptr, jint funcindex, jint n)
{
   lua_State * L = (lua_State*) cptr;
   const char * str = lua_getupvalue(L, funcindex, n);
   return (*env)->NewStringUTF(env, str);
}

/*
 * Class:     org_lua_commons_impl_nativelua_NativeLuaStateApi
 * Method:    _setupvalue
 * Signature: (JII)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_lua_commons_impl_nativelua_NativeLuaStateApi__1setupvalue
  (JNIEnv * env, jobject jobj, jlong cptr, jint funcindex, jint n)
{
   lua_State * L = (lua_State*) cptr;
   const char * str = lua_setupvalue(L, funcindex, n);
   return (*env)->NewStringUTF(env, str);
}

