package io.github.erfangc.finser.proforma

import io.github.erfangc.raincove.sdk.models.CashflowStatement
import io.github.erfangc.raincove.sdk.models.FinancialStatement

class CashflowStatementProjector {
    fun projectCashflowStatement(financialStatement: FinancialStatement, assumptions: Assumptions): CashflowStatement {
        val incomeStatement = financialStatement.incomeStatement
        val balanceSheet = financialStatement.balanceSheet
        val (externalAssumptions, derivedAssumptions) = assumptions
        val revenueGrowth = externalAssumptions.revenueGrowth
        val (_, _, _, _, _, depreciationAsPctOfAssets) = derivedAssumptions

        return CashflowStatement().apply {
            netIncome = incomeStatement.netIncome
            depreciation = depreciationAsPctOfAssets * balanceSheet.totalAssets
            // just these for now
            capitalExpenditures = (1 + revenueGrowth) * financialStatement.cashflowStatement.capitalExpenditures
        }
    }
}