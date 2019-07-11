package io.github.erfangc.finser.proforma

import io.github.erfangc.raincove.sdk.models.BalanceSheet
import io.github.erfangc.raincove.sdk.models.CashflowStatement
import io.github.erfangc.raincove.sdk.models.FinancialStatement
import io.github.erfangc.raincove.sdk.models.IncomeStatement

object ZeroOut {

    fun zeroOut(financialStatement: FinancialStatement): FinancialStatement {
        return FinancialStatement().apply {
            this.companyId = financialStatement.companyId
            this.id = financialStatement.id
            this.filingType = financialStatement.filingType
            this.url = financialStatement.url
            this.date = financialStatement.date
            this.createdOn = financialStatement.createdOn
            this.createdBy = financialStatement.createdBy
            this.updatedOn = financialStatement.updatedOn
            this.updatedBy = financialStatement.updatedBy
            this.incomeStatement = zeroOut(financialStatement.incomeStatement)
            this.balanceSheet = zeroOut(financialStatement.balanceSheet)
            this.cashflowStatement = zeroOut(financialStatement.cashflowStatement)
        }
    }

    private fun zeroOut(cashflowStatement: CashflowStatement): CashflowStatement {
        return CashflowStatement().apply {
            netIncome = cashflowStatement.netIncome ?: 0.0
            depreciation = cashflowStatement.depreciation ?: 0.0
            adjustmentsToNetIncome = cashflowStatement.adjustmentsToNetIncome ?: 0.0
            changesInAccountsReceivables = cashflowStatement.changesInAccountsReceivables ?: 0.0
            changesInLiabilities = cashflowStatement.changesInLiabilities ?: 0.0
            changesInInventories = cashflowStatement.changesInInventories ?: 0.0
            changesInOtherOperatingActivities = cashflowStatement.changesInOtherOperatingActivities ?: 0.0
            totalCashFlowFromOperatingActivities = cashflowStatement.totalCashFlowFromOperatingActivities ?: 0.0
            capitalExpenditures = cashflowStatement.capitalExpenditures ?: 0.0
            investments = cashflowStatement.investments ?: 0.0
            otherCashFlowsFromInvestingActivities = cashflowStatement.otherCashFlowsFromInvestingActivities ?: 0.0
            totalCashFlowsFromInvestingActivities = cashflowStatement.totalCashFlowsFromInvestingActivities ?: 0.0
            dividendsPaid = cashflowStatement.dividendsPaid ?: 0.0
            netBorrowings = cashflowStatement.netBorrowings ?: 0.0
            totalCashFlowsFromFinancingActivities = cashflowStatement.totalCashFlowsFromFinancingActivities ?: 0.0
            changeInCashAndCashEquivalents = cashflowStatement.changeInCashAndCashEquivalents ?: 0.0
        }
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

}