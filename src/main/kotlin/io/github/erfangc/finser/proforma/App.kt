package io.github.erfangc.finser.proforma

import feign.FeignException
import io.github.erfangc.raincove.sdk.apis.RaincoveSdk
import io.github.erfangc.raincove.sdk.models.FinancialStatement
import io.github.erfangc.raincove.sdk.models.IncomeStatement

fun main() {
    val finser = RaincoveSdk.finser()
    try {
        val aapl = finser.getCompany("AAPL")
        val financialStatements = finser.getFinancialStatements("AAPL").financialStatements
        projectFinancialStatements(financialStatements)
    } catch (e: FeignException) {
        println(e.contentUTF8())
    }

}

private fun projectFinancialStatements(financialStatements: MutableList<FinancialStatement>) {
    val mostRecent = financialStatements.last()

    val t0 = mostRecent.incomeStatement
    val t0B = mostRecent.balanceSheet

    // external assumptions
    val revenueGrowth = 0.06 // 6%
    val taxRate = 0.183 // 18.3%
    val interestRate = t0.interestExpense / (t0B.longTermDebt + t0B.shortOrCurrentLongTermDebt)

    // derived assumptions
    val grossMargin = t0.costOfRevenue / t0.totalRevenue

    val prev = t0
    val prevB = t0B

    val t1 = IncomeStatement().apply {
        totalRevenue = prev.totalRevenue * (1 + revenueGrowth)
        costOfRevenue = grossMargin * revenueGrowth
        grossProfit = totalRevenue - costOfRevenue
        // TODO this is where the "model class" can determine how to compute these
        researchDevelopment = prev.researchDevelopment * (1 + revenueGrowth)
        sellingGeneralAndAdministrative = prev.sellingGeneralAndAdministrative * (1 + revenueGrowth)
        totalOperatingExpenses = researchDevelopment + sellingGeneralAndAdministrative
        operatingIncomeOrLoss = grossProfit - totalOperatingExpenses

        totalOtherIncomeOrExpensesNet = prev.totalOtherIncomeOrExpensesNet * (1 + revenueGrowth)
        earningsBeforeInterestAndTaxes = operatingIncomeOrLoss - totalOtherIncomeOrExpensesNet
        interestExpense = (prevB.shortOrCurrentLongTermDebt + prevB.longTermDebt) * interestRate // TODO
        incomeBeforeTax = earningsBeforeInterestAndTaxes - interestExpense
        incomeTaxExpense = taxRate * incomeBeforeTax

        netIncomeFromContinuingOps = incomeBeforeTax - incomeTaxExpense
    }
    println(t1)
}