class BankAccount(amount: Int) {

    var balance: Int = amount
        private set(value) {
            logTransaction(field, value)
            field = value
        }

    fun deposit(amount: Int) {
        require(amount > 0) { "Amount should be a positive number" }
        balance += amount
    }

    fun withdraw(amount: Int) {
        require(amount > 0) { "Amount should be a positive number" }
        require(amount <= balance) { "Amount should be lesser than balance" }
        balance -= amount
    }

    init {
        require(amount > 0) { "Amount should be a positive number" }
    }
}