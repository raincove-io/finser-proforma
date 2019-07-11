package io.github.erfangc.finser.proforma

import io.github.erfangc.raincove.sdk.models.FinancialStatement

class FCFProjector {
    fun projectFCF(financialStatement: FinancialStatement, prevFinancialStatement: FinancialStatement): FCF {
        val incomeStatement = financialStatement.incomeStatement
        val balanceSheet = financialStatement.balanceSheet
        val cashflowStatement = financialStatement.cashflowStatement

        val capex = cashflowStatement.capitalExpenditures

        // CA
        val prevCA = prevFinancialStatement.balanceSheet.totalCurrentAssets
        val ca = balanceSheet.totalCurrentAssets
        val changeInCurrentAssets = ca - prevCA

        // CL
        val prevCL = prevFinancialStatement.balanceSheet.totalCurrentLiabilities
        val cl = balanceSheet.totalCurrentLiabilities
        val changeInCurrentLiabilities = cl - prevCL

        val netIncome = incomeStatement.netIncome
        val depreciation = cashflowStatement.depreciation
        val interestExpense = incomeStatement.interestExpense

        val fcf = netIncome
        +depreciation
        +interestExpense
        -changeInCurrentAssets
        +changeInCurrentLiabilities
        -capex

        return FCF(
                value = fcf,
                capex = capex,
                depreciation = depreciation,
                interestExpense = interestExpense,
                changeInCurrentAssets = changeInCurrentAssets,
                changeInCurrentLiabilities = changeInCurrentLiabilities
        )
    }
}