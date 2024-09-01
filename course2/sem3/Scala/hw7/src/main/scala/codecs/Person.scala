package codecs

trait Person {
  def name: String
  def age: Int
}
object Person {
  case class University(name: String, city: String, country: String, qsRank: Int)
  case class Student(name: String, age: Int, university: University) extends Person

  trait Worker extends Person {
    def salary: Double
  }
  case class Employee(name: String, age: Int, salary: Double) extends Worker
  case class Manager(name: String, age: Int, salary: Double, employees: List[Employee], boss: Option[Manager])
    extends Worker
}
