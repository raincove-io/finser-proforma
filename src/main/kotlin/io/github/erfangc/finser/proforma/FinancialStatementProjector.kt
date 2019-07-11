package io.github.erfangc.finser.proforma

import io.github.erfangc.finser.proforma.ZeroOut.zeroOut
import io.github.erfangc.raincove.sdk.models.FinancialStatement

data class ProjectionOutput(val projections: List<Projection>)

data class FCF(
        val value: Double,
        val capex: Double,
        val depreciation: Double,
        val interestExpense: Double,
        val changeInCurrentAssets: Double,
        val changeInCurrentLiabilities: Double
)

data class Projection(val period: Int, val financialStatement: FinancialStatement, val fcf: FCF)

data class ProjectionInput(val periods: Int = 4, val assumptions: Assumptions, val historicalFinancialStatements: List<FinancialStatement>)

class FinancialStatementProjector {
    fun projectFinancialStatements(input: ProjectionInput): ProjectionOutput {
        val (periods, assumptions, historicalFinancialStatements) = input

        val isp = IncomeStatementProjector()
        val bsp = BalanceSheetProjector()
        val cfsp = CashflowStatementProjector()
        val fcfp = FCFProjector()

        //
        // project for the next N years
        //
        val tMinus2 = historicalFinancialStatements
                .getOrNull(historicalFinancialStatements.size - 2)
                ?: FinancialStatement()
        val initial = listOf(
                Projection(
                        period = 0,
                        financialStatement = zeroOut(historicalFinancialStatements.last()),
                        fcf = fcfp.projectFCF(zeroOut(historicalFinancialStatements.last()), zeroOut(tMinus2))
                )
        )

        val projections = (1..periods).fold(initial, { acc, i ->
            val lastFinancialStatement = zeroOut(acc.last().financialStatement)
            val projectedFinancialStatement = FinancialStatement().apply {
                incomeStatement = isp.nextYearIncomeStatement(lastFinancialStatement, assumptions)
                balanceSheet = bsp.nextYearBalanceSheet(lastFinancialStatement, incomeStatement, assumptions)
                cashflowStatement = cfsp.projectCashflowStatement(lastFinancialStatement, assumptions)
            }
            val fcf = fcfp.projectFCF(projectedFinancialStatement, lastFinancialStatement)
            acc + Projection(period = i, financialStatement = projectedFinancialStatement, fcf = fcf)
        })

        return ProjectionOutput(projections = projections)
    }
}