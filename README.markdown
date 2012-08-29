# Lua-commons

Lua-commons is a lua framework for java, that allows to use lua scripts in your java applications. Main feature of a project is using lua as server's communication language (see more in "Lua in Clouds" part). Project distributes under MIT License. Framework is currently under development.

## Framework Architecture

Framework has 3 levels:

* Native level
* Base level
* Custom level

### Native level

Native level implements main functions to work with lua. Native level has only interfaces. In current time, native level has one implementation - native lua 5.2. In this implementation java loads system lua library and use native interface for working with it. Jar with system lua libraries contains system libraries for different architectures and operation systems and loads one of them depends on system properties *os.name* and *os.arch*. In future, could be created native level implementation in pure java for applications with strict requirements of cross-platforming.

*NOTE: Jar with native libraries for different architectures and operation systems don't created yet, because native interface is under developing. You can build jar for your system (Linux 32/64) by maven from sources (see readme file in lua-commons-impl/lua-commons-impl-lua-5.2)*

### Base level

Base level uses native level to work with lua and implements main classes for handling lua types to java types and other services. If you need high performance you can use this level of framework.

### Custom level

Custom level uses base level and native level to work with lua and contains classes that allows to integrate lua to java with high level of abstractions. For example, this level contains classes that allows to export any java functions to lua. Framework automatically checks method signatures and cast arguments from lua types to java types.

This level contains many reflections and other operations that decreases performance. So, if you need high performance, you must use *Base Level* or *Native Level*.

# Main features

Lua-commons project contains some useful examples of using framework. This tools can be used in java applications without studying how framework works.

## Lua in Clouds

One of the main idea of this framework to use lua in clouds as transport language. It's mix of RPC and REST architectures.

Why it's not RPC:
1. Components don't depends by hardware or OS, because Lua has a own VM that works on many popular platforms;
2. One server couldn't send any object to other server, for sending objects framework uses only JSON representation;
3. Different servers haven't shared objects or other resources.

Why it's not REST:
1. You don't need to create more REST services on many servers to implement new feature, or change of response;
2. Your code stored only in one place, and can be easily changed;
3. Components in cloud can be more reusable without any changings, because its have full private api (like DSL).

It is NOT alternative for REST architecture. REST services are still good choice for interacting users with system. This architecture must used as internal protocol, for integration different components in solid system.

### So, what is it?

Each private service must have one endpoint, and if you need to use this service, you must send Lua function with parameters to private service. This Lua function executes in prepared safe environment and sent result as response. This feature allows to store all business logic in separated server, and easily updated.

This architecture may be compared with relative database management systems, which can be controlled by SQL scripts.

## How to use

First of all, your application must have as dependency framework jars and one of lua implementation.
If you use maven your *pom.xml* may have next lines:

```text
<dependency>
   <groupId>org.lua.commons</groupId>
   <artifactId>lua-commons-base-level</artifactId>
</dependency>
<dependency>
   <groupId>org.lua.commons.impl</groupId>
   <artifactId>lua-commons-impl-lua-java</artifactId>
</dependency>
```

Before using lua in your java application, you must choose native implementation like this:
```java
LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
```

Main class of a lua in base level is *org.lua.commons.baseapi.Lua*. This class allows to execute scripts, and contains helpful services. To create this class you must do next lines:

```java
lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
lua.addExtension(LuaReferencesStorage.class, new WeakLuaReferencesStorage(lua));
lua.addExtension(LuaMetatablesService.class, new SimpleLuaMetatablesService());
lua.addExtension(TypeCastManager.class, new SimpleTypeCastManager()); // useful only for custom level
lua.start();
```

When you create Lua object, you can work with lua from java. In lua-commons library uses extension methods architecture, so you can use static methods from special classes to work with Lua object.

Framework supports lua threads. When you need to work with lua stack, you must use *org.lua.commons.baseapi.LuaThread* class. You can receive this object by two paths:

* You can use thread-context LuaThread object. Lua contains LuaContextThreadPool that stores separate LuaThread objects for each jvm threads. When you need LuaThread object you can get context LuaThread from pool by method Lua#contextThread() do what you need and forget this object. LuaThread objects from ContextLuaThreadPool are reusable, so your code should not store context LuaThread object received from Lua#contextThread() in object fields!

* If you need separate LuaThread object, that should not used by others, you can create this thread by method BaseLuaThread#newThread() from thread-context LuaThread.

LuaThread has two main fields for working with lua: state and stack. This fields returns LuaState object and LuaStack objects. LuaState contains methods for working with lua like: getGlobal, setGlobal, getMetatable and others. LuaStack contains methods for push/pop operations with lua stack.

## Static methods

### Base level

1. BaseLuaTools:
1.1. openLibs - opens standart lua libraries;
1.2. doFile - executes lua script from file;
1.3. doString - executes lua script from string;
1.4. loadFile - loads lua script from file but not execute it (load as function);
1.5. loadString - loads lua script from file but not execute it (loads as function);
1.6. getGlobalsTable - returns lua globals table;
1.7. setGlobalsTable - sets lua globals table.
2. BaseLuaThread:
2.1. newThread - creates new lua thread.
3. LuaStack - functions for working with lua stack.

### Custom level

1. CustomLuaTools:
1.1. castFrom - cast object from lua to java;
1.2. castTo - cast object from java to lua.
2. SandboxLuaTools:
2.1. safeThread - block using some lua functions for same thread;
2.2. newSafeThread - create new thread with some blocked lua functions.

## Lua type wrappers

Lua-commons contains next wrappers for lua types: LuaBoolean, LuaNumber, LuaString, LuaTable, LuaFunction, LuaJavaObject. Also, lua-commons contains LuaMetatable wrapper, but this class is wraps tables in lua like LuaTable and just implements some useful methods. All functions in lua (lua or java) represents by single LuaFunction. All wrapper classes extends LuaObject.

If you need to cast some primitive java object to one of this wrappers, you can use one of static methods LuaObjectTools#tolua, or use LuaObjectTools#fromlua to cast lua object to wrapper.

## Using java functions in lua

In base level you can use from lua only java functions with signature int(LuaThread). You can find arguments in lua stack of a thread. All results of a function you must push to lua stack and return its count. Use LuaStack#pushJavaFunction method to push java function to lua.

In custom level you can add any java functions to lua and lua-commons try to cast arguments for java objects. Use CustomJavaFunction object (you must create it and use method CustomJavaFunction#function) to push java function to lua. Lua objects casts to java by TypeCastManager. By default TypeCastManager can cast any primitive objects (booleans, numbers, strings) and containers (collections, lists, sets, maps). To cast specific objects you can add handler for TypeCastManager by lua.getExtension(TypeCastManager.class).addHandler(handler) method.

