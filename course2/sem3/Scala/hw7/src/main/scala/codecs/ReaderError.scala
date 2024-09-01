package codecs

sealed trait ReaderError
final case class AbsentField(name: String) extends ReaderError
final case class WrongType(field: String, message: String = "Wrong field type") extends ReaderError
