package io.github.erfangc.finser.proforma

import io.github.erfangc.raincove.sdk.models.BalanceSheet
import io.github.erfangc.raincove.sdk.models.FinancialStatement
import io.github.erfangc.raincove.sdk.models.IncomeStatement

class BalanceSheetProjector {
    fun nextYearBalanceSheet(mostRecent: FinancialStatement,
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

    companion object {
        fun currentLiabilities(mostRecentBalanceSheet: BalanceSheet) =
                mostRecentBalanceSheet.accountsPayable + mostRecentBalanceSheet.otherCurrentAssets + mostRecentBalanceSheet.inventory + mostRecentBalanceSheet.netReceivables

        fun currentAssets(mostRecentBalanceSheet: BalanceSheet) =
                mostRecentBalanceSheet.cashAndCashEquivalents + mostRecentBalanceSheet.inventory + mostRecentBalanceSheet.shortTermInvestments + mostRecentBalanceSheet.netReceivables + mostRecentBalanceSheet.otherCurrentAssets
    }
}