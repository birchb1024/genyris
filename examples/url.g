class URL()
   def .valid?(str)
      str (.match "http://[^/]+/.*")
   def .getProtocol()
      left (.split ":")
   def .getServer()
      nth 2 (.split "/")

define myurl "http://foo/bar.html"
tag URL myurl
assertEqual "http" (myurl(.getProtocol))    
assertEqual "foo" (myurl(.getServer)) 
  