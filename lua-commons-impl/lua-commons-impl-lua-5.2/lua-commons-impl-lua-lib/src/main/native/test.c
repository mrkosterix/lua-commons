#include "include/lua.h"
#include "include/lauxlib.h"
#include <stdio.h>

char* tmpfilename;
FILE* tmpf;
int buflen;

void startDump() {
printf("starting\n");
   //tmpfilename = tmpnam(NULL);
   tmpfilename = "test.func";
printf("tmpfilename %s\n", tmpfilename);
   tmpf = fopen(tmpfilename, "wb");
   buflen = 0;
printf("started\n");
}

void* finishDump() {
printf("finishing\n");
   fclose(tmpf);
printf("finished\n");
   tmpf = fopen(tmpfilename, "rb");
   void* buf = malloc(buflen + 1);
   fread(buf, buflen, 1, tmpf);
   fclose(tmpf);
   return buf;
}

static int writer(lua_State * L, const void * buf, size_t size, void * unused)
{
printf("write\n");
    fwrite(buf, 1, size, tmpf);
    buflen += size;
    return 0;
}


void dump_buffer(void *buffer, int buffer_size)
{
  int i;

  for(i = 0;i < buffer_size;++i)
     printf("%c", ((char *)buffer)[i]);
}

int main(int argc, char** argv)
{
   lua_State * L = luaL_newstate();
   luaL_openlibs(L);

   if (argc > 1) {
   printf("load\n");
   luaL_loadfile(L, "test.func");
   lua_setglobal(L, "test");

   lua_getglobal(L, "test");
   //lua_getglobal(L, "_ENV");
   lua_newtable(L);
   lua_pushstring(L, "print");
   lua_getglobal(L, "print");
   printf("%d\n", lua_isfunction(L, -1));
   lua_settable(L, -3);
   //lua_pushnumber(L, 5);
   printf("%s", lua_setupvalue(L, -2, 1));
   lua_getupvalue(L, -1, 1);
   printf(" %ld\n", lua_tonumber(L, -1));

   lua_getglobal(L, "test");
   lua_pushnumber(L, 5);
   printf("%s", lua_setupvalue(L, -2, 2));
   lua_getupvalue(L, -1, 2);
   printf(" %f\n", lua_tonumber(L, -1));

   lua_getglobal(L, "test");
   lua_pushnumber(L, 6);
   printf("%s", lua_setupvalue(L, -2, 3));
   lua_getupvalue(L, -1, 3);
   printf(" %f\n", lua_tonumber(L, -1));

   lua_getglobal(L, "test");
   //lua_getglobal(L, "test");
   lua_call(L, 0, 0);
   }
   else {
   printf("save\n");
   luaL_dofile(L, "test.lua");
   luaL_dostring(L, "clos = test(5, 6)");
   lua_getglobal(L, "clos");
   remove("test.func");
printf("beforemove\n");
   startDump();
   lua_dump(L, writer, NULL);
   void* buf = finishDump();
   dump_buffer(buf, buflen);
   
   luaL_dostring(L, "result = clos()");
   }

/*
   luaL_dofile(L, "test.lua");
   //luaL_dostring(L, "hello = \"asdf\"");
   luaL_dostring(L, "clos = test(5, 6)");
   lua_getglobal(L, "clos");
   remove("test.func");
   lua_dump(L, writer, NULL);
   luaL_dostring(L, "result = clos()");
*/

   return 0;
}
