package io.github.erfangc.finser.proforma

import io.github.erfangc.raincove.sdk.models.FinancialStatement
import io.github.erfangc.raincove.sdk.models.IncomeStatement

class IncomeStatementProjector {
    fun nextYearIncomeStatement(mostRecent: FinancialStatement,
                                assumptions: Assumptions): IncomeStatement {
        val prev = mostRecent.incomeStatement
        val prevB = mostRecent.balanceSheet

        // external assumptions
        val revenueGrowth = assumptions.externalAssumptions.revenueGrowth
        val taxRate = assumptions.derivedAssumptions.taxRate
        val interestRate = assumptions.derivedAssumptions.interestRate
        // derived assumptions
        val grossMargin = assumptions.derivedAssumptions.grossMargin

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

            netIncome = netIncomeFromContinuingOps
        }
        return t1
    }
}