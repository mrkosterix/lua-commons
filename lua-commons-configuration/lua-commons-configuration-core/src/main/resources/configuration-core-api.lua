function async(x, a1, a2, a3, a4, a5, a6, a7, a8, a9)
  return function()
    return x(a1, a2, a3, a4, a5, a6, a7, a8, a9)
  end
end