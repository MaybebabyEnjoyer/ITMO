package typeparams

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ServiceLevelAdvanceSpec extends AnyFlatSpec with Matchers {

  "ServiceLevelAdvance" should "allow advancing to a higher service level" in {
    val economy = new ServiceLevelAdvance[Economy]
    val upgradedEconomy = economy.advance[UpgradedEconomy]
    val special1b = upgradedEconomy.advance[Special1b]
    val extended = special1b.advance[ExtendedEconomy]
    val business = extended.advance[Business]
    val elite = business.advance[Elite]
    val _ = elite.advance[Platinum]
  }

  it should "not compile when trying to downgrade service level" in {
    "val economy = new ServiceLevelAdvance[Elite].advance[Economy]" shouldNot typeCheck
  }
}
