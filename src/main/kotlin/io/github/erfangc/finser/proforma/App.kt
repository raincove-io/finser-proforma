package io.github.erfangc.finser.proforma

import com.fasterxml.jackson.databind.ObjectMapper
import feign.FeignException
import io.github.erfangc.finser.proforma.BalanceSheetProjector.Companion.currentAssets
import io.github.erfangc.finser.proforma.BalanceSheetProjector.Companion.currentLiabilities
import io.github.erfangc.raincove.sdk.apis.RaincoveSdk
import io.github.erfangc.raincove.sdk.models.FinancialStatement
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("App")

fun main() {
    val finser = RaincoveSdk.finser()
    try {
        val financialStatements = finser.getFinancialStatements("AAPL").financialStatements
        runDCF(financialStatements)
    } catch (e: FeignException) {
        println(e.contentUTF8())
    }
}

private fun runDCF(financialStatements: MutableList<FinancialStatement>) {
    val fsp = FinancialStatementProjector()
    val projectionOutput = fsp
            .projectFinancialStatements(
                    ProjectionInput(
                            assumptions = getAssumptions(financialStatements),
                            historicalFinancialStatements = financialStatements
                    )
            )
    projectionOutput
    val projectedStatements = projectionOutput.projections.map { it.financialStatement }
    logger.info(ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(projectedStatements))
    //
    // compute a WACC
    //

    //
    // compute terminal value
    //

    //
    // compute a valuation and related metrics as output
    //
}

private fun getAssumptions(financialStatements: MutableList<FinancialStatement>): Assumptions {
    val mostRecentFinancialStatement = financialStatements.last()
    val mostRecentCashflowStatement = mostRecentFinancialStatement.cashflowStatement
    val mostRecentIncomeStatement = mostRecentFinancialStatement.incomeStatement
    val mostRecentBalanceSheet = mostRecentFinancialStatement.balanceSheet
    val currentAssets = mostRecentBalanceSheet.totalCurrentAssets ?: currentAssets(mostRecentBalanceSheet)
    val currentLiabilities = mostRecentBalanceSheet.totalCurrentLiabilities
            ?: currentLiabilities(mostRecentBalanceSheet)
    return Assumptions(
            externalAssumptions = ExternalAssumptions(revenueGrowth = 0.06),
            derivedAssumptions = DerivedAssumptions(
                    taxRate = mostRecentIncomeStatement.incomeTaxExpense / mostRecentIncomeStatement.incomeBeforeTax,
                    grossMargin = mostRecentIncomeStatement.costOfRevenue / mostRecentIncomeStatement.totalRevenue,
                    interestRate = mostRecentIncomeStatement.interestExpense / (mostRecentBalanceSheet.longTermDebt + mostRecentBalanceSheet.shortOrCurrentLongTermDebt),
                    currentAssetRatio = currentAssets / mostRecentIncomeStatement.totalRevenue,
                    currentLiabilityRatio = currentLiabilities / mostRecentIncomeStatement.totalRevenue,
                    depreciationAsPctOfAssets = mostRecentCashflowStatement.depreciation / mostRecentBalanceSheet.totalAssets - currentAssets
            )
    )
}
