package no.tordly.either.account

import no.tordly.either.Either
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.util.*

data class Account(val balance: BigDecimal) {
    companion object {
        fun create(initialBalance: BigDecimal): Either<AccountError.NegativeAmount, Account> =
            applyAmount(initialBalance) { Account(it) }

        private fun applyAmount(amount: BigDecimal, fn: (BigDecimal) -> Account) =
            if (amount < ZERO) Either.Left(AccountError.NegativeAmount) else Either.Right(fn(amount))
    }

    fun deposit(amount: BigDecimal): Either<AccountError.NegativeAmount, Account> =
        applyAmount(amount) { this.copy(balance = this.balance + it) }

    fun withdraw(amount: BigDecimal): Either<AccountError, Account> =
        applyAmount(amount) { this.copy(balance = this.balance - it) }
            .flatMap {
                if ((balance - amount) < ZERO) Either.Left(AccountError.NotEnoughFunds)
                else Either.Right(Account(balance - amount))
            }
}

sealed class AccountError {

    object NegativeAmount : AccountError()
    object NotEnoughFunds : AccountError()
    object AccountNotFound : AccountError()
}

interface AccountRepository {
    fun findBy(userId: UUID): Either<AccountError.AccountNotFound, Account>
    fun save(account: Account)
}