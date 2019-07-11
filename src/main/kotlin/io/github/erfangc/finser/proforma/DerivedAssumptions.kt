package io.github.erfangc.finser.proforma

data class DerivedAssumptions(
        val taxRate: Double,
        val interestRate: Double,
        val grossMargin: Double,
        val currentAssetRatio: Double,
        val currentLiabilityRatio: Double,
        val depreciationAsPctOfAssets: Double
)