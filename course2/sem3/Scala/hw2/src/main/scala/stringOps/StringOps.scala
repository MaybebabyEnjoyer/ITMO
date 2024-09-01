package stringOps

object StringOps {
  def duplicateString(str: String): String = str + str

  def cutString(str: String): String = str.substring(0, str.length / 2)

  def reverseString(str: String): String = str.reverse

  def transformString(str: String, f: String => String): String = f(str)
}
