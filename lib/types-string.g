@prefix : "http://www.genyris.org/lang/types#"

class :String()
  def .valid?(obj)
    is-instance? obj String

class :URL(:String)
  # HTML URLs
  def .valid?(obj)
    obj(.match "(https?|ftp)://[^\/]*")