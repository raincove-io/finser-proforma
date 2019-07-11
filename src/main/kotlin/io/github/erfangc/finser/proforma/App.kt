package io.github.erfangc.finser.proforma

import feign.FeignException
import io.github.erfangc.raincove.sdk.apis.RaincoveSdk
import io.github.erfangc.raincove.sdk.models.BalanceSheet
import io.github.erfangc.raincove.sdk.models.FinancialStatement
import io.github.erfangc.raincove.sdk.models.IncomeStatement

internal data class ExternalAssumptions(val revenueGrowth: Double)
internal data class DerivedAssumptions(
        val taxRate: Double,
        val interestRate: Double,
        val grossMargin: Double,
        val currentAssetRatio: Double,
        val currentLiabilityRatio: Double,
        val depreciationAsPctOfAssets: Double
)

internal data class Assumptions(val externalAssumptions: ExternalAssumptions, val derivedAssumptions: DerivedAssumptions)

fun main() {
    val finser = RaincoveSdk.finser()
    try {
        val financialStatements = finser.getFinancialStatements("AAPL").financialStatements
        projectFinancialStatements(financialStatements)
    } catch (e: FeignException) {
        println(e.contentUTF8())
    }
}

private fun projectFinancialStatements(financialStatements: MutableList<FinancialStatement>) {
    val mostRecentFinancialStatement = financialStatements.last()
    val mostRecentCashflowStatement = mostRecentFinancialStatement.cashflowStatement
    val mostRecentIncomeStatement = mostRecentFinancialStatement.incomeStatement
    val mostRecentBalanceSheet = mostRecentFinancialStatement.balanceSheet
    val currentAssets = mostRecentBalanceSheet.totalCurrentAssets ?: currentAssets(mostRecentBalanceSheet)
    val currentLiabilities = mostRecentBalanceSheet.totalCurrentLiabilities
            ?: currentLiabilities(mostRecentBalanceSheet)
    val assumptions = Assumptions(
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
    //
    // project for the next N years
    //
    val n = 4
    val initial = listOf(n to mostRecentFinancialStatement)
    val projected = (1..n).fold(initial, { acc, i ->
        val mostRecent = FinancialStatement().apply {
            incomeStatement = zeroOut(acc.last().second.incomeStatement)
            balanceSheet = zeroOut(acc.last().second.balanceSheet)
        }
        acc + (i to FinancialStatement().apply {
            incomeStatement = nextYearIncomeStatement(mostRecent, assumptions)
            balanceSheet = nextYearBalanceSheet(mostRecent, incomeStatement, assumptions)
        })
    })

    //
    // compute a WACC
    //

    //
    // compute FCFs
    //

    //
    // compute terminal value
    //

    //
    // compute a valuation and related metrics as output
    //
}

private fun zeroOut(incomeStatement: IncomeStatement): IncomeStatement {
    return IncomeStatement().apply {
        totalRevenue = incomeStatement.totalRevenue ?: 0.0
        costOfRevenue = incomeStatement.costOfRevenue ?: 0.0
        grossProfit = incomeStatement.grossProfit ?: 0.0
        researchDevelopment = incomeStatement.researchDevelopment ?: 0.0
        sellingGeneralAndAdministrative = incomeStatement.sellingGeneralAndAdministrative ?: 0.0
        totalOperatingExpenses = incomeStatement.totalOperatingExpenses ?: 0.0
        operatingIncomeOrLoss = incomeStatement.operatingIncomeOrLoss ?: 0.0
        totalOtherIncomeOrExpensesNet = incomeStatement.totalOtherIncomeOrExpensesNet ?: 0.0
        earningsBeforeInterestAndTaxes = incomeStatement.earningsBeforeInterestAndTaxes ?: 0.0
        interestExpense = incomeStatement.interestExpense ?: 0.0
        incomeBeforeTax = incomeStatement.incomeBeforeTax ?: 0.0
        incomeTaxExpense = incomeStatement.incomeTaxExpense ?: 0.0
        netIncomeFromContinuingOps = incomeStatement.netIncomeFromContinuingOps ?: 0.0
        netIncome = incomeStatement.netIncome ?: 0.0
        netIncomeApplicableToCommonShares = incomeStatement.netIncomeApplicableToCommonShares ?: 0.0
    }
}

private fun zeroOut(balanceSheet: BalanceSheet): BalanceSheet {
    return BalanceSheet().apply {
        cashAndCashEquivalents = balanceSheet.cashAndCashEquivalents ?: 0.0
        shortTermInvestments = balanceSheet.shortTermInvestments ?: 0.0
        netReceivables = balanceSheet.netReceivables ?: 0.0
        inventory = balanceSheet.inventory ?: 0.0
        otherCurrentAssets = balanceSheet.otherCurrentAssets ?: 0.0
        totalCurrentAssets = balanceSheet.totalCurrentAssets ?: 0.0
        longTermInvestments = balanceSheet.longTermInvestments ?: 0.0
        propertyPlantAndEquipment = balanceSheet.propertyPlantAndEquipment ?: 0.0
        otherAssets = balanceSheet.otherAssets ?: 0.0
        totalAssets = balanceSheet.totalAssets ?: 0.0
        capitaSurplus = balanceSheet.capitaSurplus ?: 0.0
        accountsPayable = balanceSheet.accountsPayable ?: 0.0
        shortOrCurrentLongTermDebt = balanceSheet.shortOrCurrentLongTermDebt ?: 0.0
        otherCurrentLiabilities = balanceSheet.otherCurrentLiabilities ?: 0.0
        totalCurrentLiabilities = balanceSheet.totalCurrentLiabilities ?: 0.0
        longTermDebt = balanceSheet.longTermDebt ?: 0.0
        otherLiabilities = balanceSheet.otherLiabilities ?: 0.0
        totalLiabilities = balanceSheet.totalLiabilities ?: 0.0
        commonStock = balanceSheet.commonStock ?: 0.0
        retainedEarnings = balanceSheet.retainedEarnings ?: 0.0
        treasuryStock = balanceSheet.treasuryStock ?: 0.0
        otherStockholderEquity = balanceSheet.otherStockholderEquity ?: 0.0
        totalStockholderEquity = balanceSheet.totalStockholderEquity ?: 0.0
        netTangibleAssets = balanceSheet.netTangibleAssets ?: 0.0
        goodwill = balanceSheet.goodwill ?: 0.0
        intangibleAssets = balanceSheet.intangibleAssets ?: 0.0
        accumulatedAmortization = balanceSheet.accumulatedAmortization ?: 0.0
        deferredLongTermAssetCharges = balanceSheet.deferredLongTermAssetCharges ?: 0.0
        redeemablePreferredStock = balanceSheet.redeemablePreferredStock ?: 0.0
        deferredLongTermLiabilityCharges = balanceSheet.deferredLongTermLiabilityCharges ?: 0.0
        minorityInterest = balanceSheet.minorityInterest ?: 0.0
        negativeGoodwill = balanceSheet.negativeGoodwill ?: 0.0
    }
}

private fun currentLiabilities(mostRecentBalanceSheet: BalanceSheet) =
        mostRecentBalanceSheet.accountsPayable + mostRecentBalanceSheet.otherCurrentAssets + mostRecentBalanceSheet.inventory + mostRecentBalanceSheet.netReceivables

private fun currentAssets(mostRecentBalanceSheet: BalanceSheet) =
        mostRecentBalanceSheet.cashAndCashEquivalents + mostRecentBalanceSheet.inventory + mostRecentBalanceSheet.shortTermInvestments + mostRecentBalanceSheet.netReceivables + mostRecentBalanceSheet.otherCurrentAssets

private fun nextYearBalanceSheet(mostRecent: FinancialStatement,
                                 currentIncomeStatement: IncomeStatement,
                                 assumptions: Assumptions): BalanceSheet {
    val prevB = mostRecent.balanceSheet
    val (externalAssumptions, derivedAssumptions) = assumptions
    val revenueGrowth = externalAssumptions.revenueGrowth
    return BalanceSheet().apply {
        // cashAndCashEquivalents # this is the plug
        shortTermInvestments = prevB.shortTermInvestments
        val currentAssetsNew = currentIncomeStatement.totalRevenue * assumptions.derivedAssumptions.currentAssetRatio
        val currentAssets = (prevB.shortTermInvestments + prevB.netReceivables + prevB.inventory + prevB.otherCurrentAssets + prevB.cashAndCashEquivalents)
        shortTermInvestments = (prevB.shortTermInvestments / currentAssets) * currentAssetsNew
        netReceivables = (prevB.netReceivables / currentAssets) * currentAssetsNew
        inventory = (prevB.inventory / currentAssets) * currentAssetsNew
        otherCurrentAssets = (prevB.otherCurrentAssets / currentAssets) * currentAssetsNew
        longTermInvestments = prevB.longTermInvestments * (1 + revenueGrowth)
        propertyPlantAndEquipment = prevB.propertyPlantAndEquipment * (1 + revenueGrowth)
        goodwill = prevB.goodwill
        intangibleAssets = prevB.intangibleAssets
        accumulatedAmortization = prevB.accumulatedAmortization * (1 + revenueGrowth)// FIXME
        otherAssets = prevB.otherAssets * (1 + revenueGrowth)
        deferredLongTermAssetCharges = prevB.deferredLongTermAssetCharges

        //
        // Liabilities modeling
        //
        val prevCurrentLiabilities = prevB.accountsPayable + prevB.shortOrCurrentLongTermDebt + prevB.otherLiabilities
        val newCurrentLiabilities = currentIncomeStatement.totalRevenue * derivedAssumptions.currentLiabilityRatio
        accountsPayable = (prevB.accountsPayable / prevCurrentLiabilities) * newCurrentLiabilities
        shortOrCurrentLongTermDebt = (prevB.shortOrCurrentLongTermDebt / prevCurrentLiabilities) * newCurrentLiabilities
        otherCurrentLiabilities = (prevB.otherCurrentLiabilities / prevCurrentLiabilities) * newCurrentLiabilities
        totalCurrentLiabilities = prevCurrentLiabilities
        longTermDebt = prevB.longTermDebt * (1 + revenueGrowth)
        otherLiabilities = prevB.otherLiabilities * (1 + revenueGrowth)
        deferredLongTermLiabilityCharges = prevB.deferredLongTermLiabilityCharges * (1 + revenueGrowth)
        minorityInterest = prevB.minorityInterest
        negativeGoodwill = prevB.negativeGoodwill
        totalLiabilities = totalCurrentLiabilities + longTermDebt + otherLiabilities + deferredLongTermAssetCharges + minorityInterest + negativeGoodwill

        //
        // shareholders' equity
        //
        redeemablePreferredStock = prevB.redeemablePreferredStock
        commonStock = prevB.commonStock
        retainedEarnings = prevB.retainedEarnings + currentIncomeStatement.netIncome // FIXME
        treasuryStock = prevB.treasuryStock
        capitaSurplus = prevB.capitaSurplus
        otherStockholderEquity = prevB.otherStockholderEquity
        totalStockholderEquity = commonStock + retainedEarnings + treasuryStock + capitaSurplus + otherStockholderEquity

        //
        // cash plug
        //
        cashAndCashEquivalents = totalStockholderEquity + totalLiabilities - (
                shortTermInvestments
                        + netReceivables
                        + inventory
                        + otherCurrentAssets
                        + longTermInvestments
                        + propertyPlantAndEquipment
                        + goodwill
                        + intangibleAssets
                        + accumulatedAmortization
                        + otherAssets
                        + deferredLongTermAssetCharges
                )

        // calculate totalCurrentAsset once cash plug is computed
        totalCurrentAssets = cashAndCashEquivalents + shortTermInvestments + netReceivables + inventory + otherCurrentAssets
        // calculate totalAssets once cash plug is computed
        totalAssets = totalStockholderEquity + totalLiabilities
    }
}

private fun nextYearIncomeStatement(mostRecent: FinancialStatement,
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