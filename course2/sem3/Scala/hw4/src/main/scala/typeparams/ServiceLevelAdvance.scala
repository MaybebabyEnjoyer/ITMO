package typeparams

class Economy

class UpgradedEconomy extends Economy

class Special1b extends UpgradedEconomy

class ExtendedEconomy extends Special1b

class Business extends ExtendedEconomy

class Elite extends Business

class Platinum extends Elite

class ServiceLevelAdvance[A <: Economy] {
  def advance[B <: A]: ServiceLevelAdvance[B] = new ServiceLevelAdvance[B]
}
