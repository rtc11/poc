package no.tordly.either.account

import no.tordly.either.Either
import org.junit.Test
import kotlin.test.assertEquals

internal class AccountTest {

    @Test
    fun `should create an account`() {
        val account = Account.create(100.toBigDecimal())
        assertEquals(Either.Right(Account(100.toBigDecimal())), account)
    }

    @Test
    fun `should fail creating an account with a negative amount`() {
        assertEquals(Either.Left(AccountError.NegativeAmount), Account.create((-100).toBigDecimal()))
    }

    @Test
    fun `should deposit money to an account`() {
        val account = Account(100.toBigDecimal())
        val updatedAccount = account.deposit(100.toBigDecimal())
        assertEquals(Either.Right(Account(200.toBigDecimal())), updatedAccount)
    }

    @Test
    fun `should fail depositing a negative amount to an account`() {
        val account = Account(100.toBigDecimal())
        val fail = account.deposit((-100).toBigDecimal())
        assertEquals(Either.Left(AccountError.NegativeAmount), fail)
    }

    @Test
    fun `should withdraw money from an account`() {
        val account = Account(100.toBigDecimal())
        val updatedAccount = account.withdraw(50.toBigDecimal())
        assertEquals(Either.Right(Account(50.toBigDecimal())), updatedAccount)
    }

    @Test
    fun `should fail withdrawing a negative amount to an account`() {
        val account = Account(100.toBigDecimal())
        val fail = account.withdraw((-50).toBigDecimal())
        assertEquals(Either.Left(AccountError.NegativeAmount), fail)
    }

    @Test
    fun `should fail withdrawing when there is not enough funds`() {
        val account = Account(100.toBigDecimal())
        val fail = account.withdraw(200.toBigDecimal())
        assertEquals(Either.Left(AccountError.NotEnoughFunds), fail)
    }
}
