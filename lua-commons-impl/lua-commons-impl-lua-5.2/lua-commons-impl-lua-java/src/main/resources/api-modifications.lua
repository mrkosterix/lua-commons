do
   local oldgetmetatable = getmetatable
   local oldsetmetatable = setmetatable

   function getmetatable(object)
      local table = oldgetmetatable(object)
      while table ~= nil do
         if table["__HIDDEN"] == nil then
            return table
         end
         table = oldgetmetatable(table)
      end
      return nil
   end

   function setmetatable(object, metatable)
      local obj = object
      local table = oldgetmetatable(obj)
      while table ~= nil do
         if table["__HIDDEN"] == nil then
            oldsetmetatable(obj, metatable)
         end
         obj = table
         table = oldgetmetatable(obj)
      end
   end
end
