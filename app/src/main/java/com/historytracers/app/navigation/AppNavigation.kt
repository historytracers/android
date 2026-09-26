// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.data.detectDefaultLanguage
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalAppCalendar
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.uiStringsForLanguage
import com.historytracers.app.ui.screens.AboutScreen
import com.historytracers.app.ui.screens.BuildingGameScreen
import com.historytracers.app.ui.screens.ContentScreen
import com.historytracers.app.ui.screens.EqualityConclusionScreen
import com.historytracers.app.ui.screens.EqualSameGroupDifferentScreen
import com.historytracers.app.ui.screens.EqualityEqualScreen
import com.historytracers.app.ui.screens.EqualityExpandingScreen
import com.historytracers.app.ui.screens.EqualityInGeneralScreen
import com.historytracers.app.ui.screens.EqualityIntroScreen
import com.historytracers.app.ui.screens.EqualityQuestionScreen
import com.historytracers.app.ui.screens.TotallyEqualConclusionScreen
import com.historytracers.app.ui.screens.TotallyEqualExerciseScreen
import com.historytracers.app.ui.screens.TotallyEqualIntroScreen
import com.historytracers.app.ui.screens.TotallyEqualQuestionScreen
import com.historytracers.app.ui.screens.TotallyEqualSignScreen
import com.historytracers.app.ui.screens.FirstStepsScreen
import com.historytracers.app.ui.screens.HistoricalEqualityConclusionScreen
import com.historytracers.app.ui.screens.HistoricalEqualityEvidenceScreen
import com.historytracers.app.ui.screens.HistoricalEqualityIntroScreen
import com.historytracers.app.ui.screens.HistoricalEqualityMappingScreen
import com.historytracers.app.ui.screens.HistoricalEqualityObjectsScreen
import com.historytracers.app.ui.screens.HistoricalEqualityPyramidsConclusionScreen
import com.historytracers.app.ui.screens.HistoricalEqualityPyramidsDefinitionScreen
import com.historytracers.app.ui.screens.HistoricalEqualityPyramidsIntroScreen
import com.historytracers.app.ui.screens.HistoricalEqualityPyramidsLookAlikeScreen
import com.historytracers.app.ui.screens.HistoricalEqualityPyramidsMesoamericaScreen
import com.historytracers.app.ui.screens.HistoricalEqualityPyramidsPrecisionScreen
import com.historytracers.app.ui.screens.HistoricalEqualityPyramidsQuestionScreen
import com.historytracers.app.ui.screens.HistoricalEqualityQuestionScreen
import com.historytracers.app.ui.screens.IndexScreen
import com.historytracers.app.ui.screens.IAmNotLikeYouScreen
import com.historytracers.app.ui.screens.IsItFreeScreen
import com.historytracers.app.ui.screens.LearningInLayersConclusionScreen
import com.historytracers.app.ui.screens.LearningInLayersIntroScreen
import com.historytracers.app.ui.screens.LearningInLayersPlayingScreen
import com.historytracers.app.ui.screens.LearningInLayersQuestionScreen
import com.historytracers.app.ui.screens.LearningInLayersStagesScreen
import com.historytracers.app.ui.screens.LearningInLayersToyScreen
import com.historytracers.app.ui.screens.LatestAdditionScreen
import com.historytracers.app.ui.screens.SettingsScreen
import com.historytracers.app.ui.screens.SequenceGameScreen
import com.historytracers.app.ui.screens.SequenceGameFamiliesScreen
import com.historytracers.app.ui.screens.WelcomeScreen
import com.historytracers.app.ui.screens.SequenceGameOrdersScreen
import com.historytracers.app.ui.screens.WorkoutScreen
import com.historytracers.app.ui.screens.AbacusScreen
import com.historytracers.app.ui.screens.ClapScreen
import com.historytracers.app.ui.screens.CongratulationScreen
import com.historytracers.app.ui.screens.ExercisingAdditionScreen
import com.historytracers.app.ui.screens.FeetAndHandsScreen
import com.historytracers.app.ui.screens.HowDoILearnChartScreen
import com.historytracers.app.ui.screens.HowDoILearnChartUnderstandingScreen
import com.historytracers.app.ui.screens.HowDoILearnComparisonsScreen
import com.historytracers.app.ui.screens.HowDoILearnDecisionScreen
import com.historytracers.app.ui.screens.HowDoILearnHorizonScreen
import com.historytracers.app.ui.screens.HowDoILearnIntroScreen
import com.historytracers.app.ui.screens.HowDoILearnQuestionScreen
import com.historytracers.app.ui.screens.MyHandsConclusionScreen
import com.historytracers.app.ui.screens.MyBodyConclusionScreen
import com.historytracers.app.ui.screens.MyBodyEverythingQuestionScreen
import com.historytracers.app.ui.screens.MyBodyFeetQuestionScreen
import com.historytracers.app.ui.screens.MyBodyFeetScreen
import com.historytracers.app.ui.screens.MyBodyImprovementScreen
import com.historytracers.app.ui.screens.MyBodyIntroScreen
import com.historytracers.app.ui.screens.FirstHandsIntroScreen
import com.historytracers.app.ui.screens.FirstHandsQuestionScreen
import com.historytracers.app.ui.screens.FirstHandsKnowledgeScreen
import com.historytracers.app.ui.screens.FirstHandsHabilisScreen
import com.historytracers.app.ui.screens.FirstHandsReflectionScreen
import com.historytracers.app.ui.screens.FirstHandsCountingScreen
import com.historytracers.app.ui.screens.FirstHandsConclusionScreen
import com.historytracers.app.ui.screens.FirstVoiceIntroScreen
import com.historytracers.app.ui.screens.FirstVoiceVoiceScreen
import com.historytracers.app.ui.screens.FirstVoiceReflectionScreen
import com.historytracers.app.ui.screens.FirstVoiceDifferenceScreen
import com.historytracers.app.ui.screens.FirstVoiceConclusionScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingIntroScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingCirclesScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingRectanglesScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingMesoamericansScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingQuestionScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingExamplesScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingConclusionScreen
import com.historytracers.app.ui.screens.NumbersIntroScreen
import com.historytracers.app.ui.screens.NumbersOriginScreen
import com.historytracers.app.ui.screens.NumbersQuestionScreen
import com.historytracers.app.ui.screens.NumbersEqualScreen
import com.historytracers.app.ui.screens.NumbersVisualizingScreen
import com.historytracers.app.ui.screens.NumbersConclusionScreen
import com.historytracers.app.ui.screens.FamilyPart1IntroScreen
import com.historytracers.app.ui.screens.FamilyPart1OrdersScreen
import com.historytracers.app.ui.screens.FamilyPart1OrderScreen
import com.historytracers.app.ui.screens.FamilyPart1AltarScreen
import com.historytracers.app.ui.screens.FamilyPart1ContinuityScreen
import com.historytracers.app.ui.screens.FamilyPart1StagesScreen
import com.historytracers.app.ui.screens.FamilyPart1RuleScreen
import com.historytracers.app.ui.screens.FamilyPart1ConclusionScreen
import com.historytracers.app.ui.screens.NaturalFamiliesPart2IntroScreen
import com.historytracers.app.ui.screens.NaturalFamiliesPart2QuestionScreen
import com.historytracers.app.ui.screens.NaturalFamiliesPart2LogicScreen
import com.historytracers.app.ui.screens.NaturalFamiliesPart2OrdersQuestionScreen
import com.historytracers.app.ui.screens.NaturalFamiliesPart2CopanScreen
import com.historytracers.app.ui.screens.NaturalFamiliesPart2NamingScreen
import com.historytracers.app.ui.screens.NaturalFamiliesPart2BillionQuestionScreen
import com.historytracers.app.ui.screens.NaturalFamiliesPart2ConclusionScreen
import com.historytracers.app.ui.screens.TheZeroIntroScreen
import com.historytracers.app.ui.screens.TheZeroWhatIsScreen
import com.historytracers.app.ui.screens.TheZeroIntuitiveScreen
import com.historytracers.app.ui.screens.TheZeroNumberScreen
import com.historytracers.app.ui.screens.TheZeroQuestionScreen
import com.historytracers.app.ui.screens.TheZeroConclusionScreen
import com.historytracers.app.ui.screens.TowardInfinityMaximumScreen
import com.historytracers.app.ui.screens.TowardInfinityLawScreen
import com.historytracers.app.ui.screens.TowardInfinityWithoutLimitsScreen
import com.historytracers.app.ui.screens.TowardInfinitySymbolScreen
import com.historytracers.app.ui.screens.TowardInfinityDirectionScreen
import com.historytracers.app.ui.screens.TowardInfinityConclusionScreen
import com.historytracers.app.ui.screens.LimitsMinMaxBetweenBothScreen
import com.historytracers.app.ui.screens.LimitsMinMaxQuestionScreen
import com.historytracers.app.ui.screens.LimitsMinMaxTendingScreen
import com.historytracers.app.ui.screens.LimitsMinMaxHandsScreen
import com.historytracers.app.ui.screens.LimitsMinMaxConclusionScreen
import com.historytracers.app.ui.screens.WhereAreTheyIntroScreen
import com.historytracers.app.ui.screens.WhereAreTheyInUsScreen
import com.historytracers.app.ui.screens.WhereAreTheyInTextsScreen
import com.historytracers.app.ui.screens.WhereAreTheyAgesScreen
import com.historytracers.app.ui.screens.WhereAreTheyQuestionScreen
import com.historytracers.app.ui.screens.WhereAreTheyWrongButRightScreen
import com.historytracers.app.ui.screens.WhereAreTheySpeciesScreen
import com.historytracers.app.ui.screens.WhereAreTheyUniverseScreen
import com.historytracers.app.ui.screens.WhereAreTheyConclusionScreen
import com.historytracers.app.ui.screens.WhereAreWeFromScreen
import com.historytracers.app.ui.screens.AnotherWayToCountScreen
import com.historytracers.app.ui.screens.BuildingLikeAMesoamericanScreen
import com.historytracers.app.ui.screens.BuildingLikeEtruscanRomansScreen
import com.historytracers.app.ui.screens.HundredsAndThousandsAppendScreen
import com.historytracers.app.ui.screens.HundredsAndThousandsConclusionScreen
import com.historytracers.app.ui.screens.HundredsAndThousandsContinueScreen
import com.historytracers.app.ui.screens.HundredsAndThousandsIntroScreen
import com.historytracers.app.ui.screens.HundredsAndThousandsModifyScreen
import com.historytracers.app.ui.screens.HundredsAndThousandsNewOldLogicScreen
import com.historytracers.app.ui.screens.HundredsAndThousandsNewOrdersScreen
import com.historytracers.app.ui.screens.HundredsAndThousandsThinkingScreen
import com.historytracers.app.ui.screens.RepresentYouScreen
import com.historytracers.app.ui.screens.EtruscanRomanTensAndNowScreen
import com.historytracers.app.ui.screens.EtruscanRomanTensBetweenScreen
import com.historytracers.app.ui.screens.EtruscanRomanTensConclusionScreen
import com.historytracers.app.ui.screens.EtruscanRomanTensIntroScreen
import com.historytracers.app.ui.screens.EtruscanRomanTensLikeBeforeScreen
import com.historytracers.app.ui.screens.EtruscanRomanTensNothingChangesScreen
import com.historytracers.app.ui.screens.EtruscanRomanTensTenNotOneScreen
import com.historytracers.app.ui.screens.EtruscanRomanTensThinkingScreen
import com.historytracers.app.ui.screens.MesoamericanOrdersAncientCalendarScreen
import com.historytracers.app.ui.screens.MesoamericanOrdersConclusionScreen
import com.historytracers.app.ui.screens.MesoamericanOrdersDifferentIsNotWrongScreen
import com.historytracers.app.ui.screens.MesoamericanOrdersFirstValueScreen
import com.historytracers.app.ui.screens.MesoamericanOrdersIntroScreen
import com.historytracers.app.ui.screens.MesoamericanOrdersItIsNotLikeThisScreen
import com.historytracers.app.ui.screens.MesoamericanOrdersLastValueScreen
import com.historytracers.app.ui.screens.MesoamericanOrdersNextOrdersScreen
import com.historytracers.app.ui.screens.MesoamericanOrdersThinkingScreen
import com.historytracers.app.ui.screens.QuipusIntroScreen
import com.historytracers.app.ui.screens.QuipusOneOfTheFirstScreen
import com.historytracers.app.ui.screens.QuipusHowItWorksScreen
import com.historytracers.app.ui.screens.QuipusSmallNumbersScreen
import com.historytracers.app.ui.screens.QuipusLargeNumbersScreen
import com.historytracers.app.ui.screens.QuipusQuestionScreen
import com.historytracers.app.ui.screens.QuipusConclusionScreen
import com.historytracers.app.ui.screens.PracticingWithQuipusScreen
import com.historytracers.app.ui.screens.SharedOriginIntroScreen
import com.historytracers.app.ui.screens.SharedOriginExpandingScreen
import com.historytracers.app.ui.screens.SharedOriginThinkScreen
import com.historytracers.app.ui.screens.SharedOriginHistoryScreen
import com.historytracers.app.ui.screens.SharedOriginCmbScreen
import com.historytracers.app.ui.screens.SharedOriginContractScreen
import com.historytracers.app.ui.screens.SharedOriginConclusionScreen
import com.historytracers.app.ui.screens.MatterAndEnergyIntroScreen
import com.historytracers.app.ui.screens.MatterAndEnergyTransformationScreen
import com.historytracers.app.ui.screens.MatterAndEnergyLawScreen
import com.historytracers.app.ui.screens.MatterAndEnergyQuestionScreen
import com.historytracers.app.ui.screens.MatterAndEnergyEnergyScreen
import com.historytracers.app.ui.screens.MatterAndEnergyTogetherScreen
import com.historytracers.app.ui.screens.MatterAndEnergyConclusionScreen
import com.historytracers.app.ui.screens.SharingWithWhomIntroScreen
import com.historytracers.app.ui.screens.SharingWithWhomNobodyScreen
import com.historytracers.app.ui.screens.SharingWithWhomThinkScreen
import com.historytracers.app.ui.screens.SharingWithWhomScenarioScreen
import com.historytracers.app.ui.screens.SharingWithWhomEmptyRegionScreen
import com.historytracers.app.ui.screens.SharingWithWhomConclusionScreen
import com.historytracers.app.ui.screens.UniverseExpansionScreen
import com.historytracers.app.ui.screens.CountingWithBonesIntroScreen
import com.historytracers.app.ui.screens.CountingWithBonesGameScreen
import com.historytracers.app.ui.screens.CountingWithBonesPracticeScreen
import com.historytracers.app.ui.screens.MyHandsCountingScreen
import com.historytracers.app.ui.screens.MyHandsFingersScreen
import com.historytracers.app.ui.screens.MyHandsIntroScreen
import com.historytracers.app.ui.screens.MyHandsQuestionScreen
import com.historytracers.app.ui.screens.SocratesConclusionScreen
import com.historytracers.app.ui.screens.SocratesMotivationScreen
import com.historytracers.app.ui.screens.SocratesPhilosophyScreen
import com.historytracers.app.ui.screens.SocratesQuestionScreen
import com.historytracers.app.ui.screens.StreakScreen
import com.historytracers.app.ui.screens.SorobanWritingScreen
import com.historytracers.app.ui.screens.SuanpanWritingScreen
import com.historytracers.app.ui.screens.SchyotyWritingScreen
import com.historytracers.app.ui.screens.LargeNumbersWritingScreen
import com.historytracers.app.ui.screens.AbacusHistoryCalculiScreen
import com.historytracers.app.ui.screens.AbacusHistoryConclusionScreen
import com.historytracers.app.ui.screens.AbacusHistoryIntroScreen
import com.historytracers.app.ui.screens.AbacusHistorySchyotyScreen
import com.historytracers.app.ui.screens.AbacusHistorySimilaritiesScreen
import com.historytracers.app.ui.screens.AbacusHistorySorobanScreen
import com.historytracers.app.ui.screens.AbacusHistorySuanpanScreen
import com.historytracers.app.ui.screens.AbacusHistoryThinkingScreen
import com.historytracers.app.ui.screens.PracticingAdditionScreen
import com.historytracers.app.ui.screens.MultiplicationTableScreen
import com.historytracers.app.ui.screens.MultiplyingWithAbacusScreen
import com.historytracers.app.ui.screens.MultiplyingWithAbacusLevel2Screen
import com.historytracers.app.ui.screens.MultiplyingWithoutLimitsScreen
import com.historytracers.app.ui.screens.SubtractingWithAbacusScreen
import com.historytracers.app.ui.screens.AddingWithAbacusScreen
import com.historytracers.app.ui.screens.ComplementToTenScreen
import com.historytracers.app.ui.screens.AddingLargeNumbersScreen
import com.historytracers.app.ui.screens.CarryingScreen
import com.historytracers.app.ui.screens.RelationshipScreen
import com.historytracers.app.ui.screens.ExercisingMultiplicationL2Screen
import com.historytracers.app.ui.screens.YupanaScreen
import com.historytracers.app.ui.screens.PracticingAdditionYupanaScreen
import com.historytracers.app.ui.screens.PracticingMultiplicationYupanaScreen
import com.historytracers.app.ui.screens.HandsOnYupanaScreen
import com.historytracers.app.ui.screens.DrawingToCountScreen
import com.historytracers.app.ui.screens.IskayMovementScreen
import com.historytracers.app.ui.screens.KimsaMovementScreen
import com.historytracers.app.ui.screens.PisqaMovementScreen
import com.historytracers.app.ui.screens.PichanaMovementScreen
import com.historytracers.app.ui.screens.CarryingInAdditionAddingScreen
import com.historytracers.app.ui.screens.CarryingInAdditionConclusionScreen
import com.historytracers.app.ui.screens.CarryingInAdditionHiddenZeroScreen
import com.historytracers.app.ui.screens.CarryingInAdditionIdentityScreen
import com.historytracers.app.ui.screens.CarryingInAdditionIntroScreen
import com.historytracers.app.ui.screens.CarryingInAdditionQuestionScreen
import com.historytracers.app.ui.screens.CarryingInAdditionZeroInHandsScreen
import com.historytracers.app.ui.screens.AddingSameNumbersIntroScreen
import com.historytracers.app.ui.screens.AddingSameNumbersBodyScreen
import com.historytracers.app.ui.screens.AddingSameNumbersThinkScreen
import com.historytracers.app.ui.screens.AddingSameNumbersCountOneScreen
import com.historytracers.app.ui.screens.AddingSameNumbersCountFiveScreen
import com.historytracers.app.ui.screens.AddingSameNumbersQuestionScreen
import com.historytracers.app.ui.screens.AddingSameNumbersNewSignScreen
import com.historytracers.app.ui.screens.AddingSameNumbersConclusionScreen
import com.historytracers.app.ui.screens.TheResultIsScreen
import com.historytracers.app.ui.screens.InversionIntroScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfZeroIntroScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfZeroRuleScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfZeroTableScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfZeroDrawingScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfZeroQuestionScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfZeroFlatLineScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfZeroConclusionScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfOneIntroScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfOneRuleScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfOneDrawingScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfOneQuestionScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfOneBoxesScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfOneConnectScreen
import com.historytracers.app.ui.screens.DrawingTheTableOfOneConclusionScreen
import com.historytracers.app.ui.screens.InversionSquaresGoingUpScreen
import com.historytracers.app.ui.screens.InversionSquaresLyingDownScreen
import com.historytracers.app.ui.screens.InversionQuestionScreen
import com.historytracers.app.ui.screens.InversionOrderScreen
import com.historytracers.app.ui.screens.InversionConclusionScreen
import com.historytracers.app.ui.screens.KinkinMovementScreen
import com.historytracers.app.ui.screens.LargeNumbersAppScreen
import com.historytracers.app.ui.screens.LargeNumbersConclusionScreen
import com.historytracers.app.ui.screens.LargeNumbersGrowingScreen
import com.historytracers.app.ui.screens.LargeNumbersIntroScreen
import com.historytracers.app.ui.screens.LargeNumbersPatternScreen
import com.historytracers.app.ui.screens.LargeNumbersQuipuScreen
import com.historytracers.app.ui.screens.MultiplyingWithYupanaConclusionScreen
import com.historytracers.app.ui.screens.MultiplyingWithYupanaFirstValuesScreen
import com.historytracers.app.ui.screens.MultiplyingWithYupanaIntroScreen
import com.historytracers.app.ui.screens.MultiplyingWithYupanaNextStepScreen
import com.historytracers.app.ui.screens.MultiplyingWithYupanaOrganizingScreen
import com.historytracers.app.ui.screens.MultiplyingWithYupanaProcessScreen
import com.historytracers.app.ui.screens.MultiplyingWithYupanaThinkingScreen
import com.historytracers.app.ui.screens.OrderOfAdditionCommutativeScreen
import com.historytracers.app.ui.screens.OrderOfAdditionConclusionScreen
import com.historytracers.app.ui.screens.OrderOfAdditionIntroScreen
import com.historytracers.app.ui.screens.OrderOfAdditionQuestionScreen
import com.historytracers.app.ui.screens.OrderOfAdditionWhereScreen
import com.historytracers.app.ui.screens.PracticingAdditionRoadScreen
import com.historytracers.app.ui.screens.PlayingWithAxiomsGameScreen
import com.historytracers.app.ui.screens.MesoamericanSymbolsConclusionScreen
import com.historytracers.app.ui.screens.MesoamericanSymbolsIntroScreen
import com.historytracers.app.ui.screens.MesoamericanSymbolsSystemScreen
import com.historytracers.app.ui.screens.MesoamericanSymbolsThinkingScreen
import com.historytracers.app.ui.screens.MesoamericanSymbolsThreeScreen
import com.historytracers.app.ui.screens.MesoamericanSymbolsVerticalScreen
import com.historytracers.app.ui.screens.MesoamericanSymbolsWriteScreen
import com.historytracers.app.ui.screens.MesoamericanSymbolsZeroScreen
import com.historytracers.app.ui.screens.OvercomingLimitsConclusionScreen
import com.historytracers.app.ui.screens.OvercomingLimitsIntroScreen
import com.historytracers.app.ui.screens.OvercomingLimitsLastFootScreen
import com.historytracers.app.ui.screens.OvercomingLimitsLogicScreen
import com.historytracers.app.ui.screens.OvercomingLimitsNextFiveScreen
import com.historytracers.app.ui.screens.OvercomingLimitsPreviousNumbersScreen
import com.historytracers.app.ui.screens.QuipuOnTheYupanaScreen
import com.historytracers.app.ui.screens.RoadToSomewhereScreen
import com.historytracers.app.ui.screens.RunningAmongNumbersAddingScreen
import com.historytracers.app.ui.screens.RunningAmongNumbersConclusionScreen
import com.historytracers.app.ui.screens.RunningAmongNumbersIntroScreen
import com.historytracers.app.ui.screens.RunningAmongNumbersQuestionScreen
import com.historytracers.app.ui.screens.RunningAmongNumbersReconstructionScreen
import com.historytracers.app.ui.screens.RunningAmongNumbersTempleScreen
import com.historytracers.app.ui.screens.RunningAndGrowingScreen
import com.historytracers.app.ui.screens.WalkAmongNumbersAxesScreen
import com.historytracers.app.ui.screens.WalkAmongNumbersConclusionScreen
import com.historytracers.app.ui.screens.WalkAmongNumbersHandsScreen
import com.historytracers.app.ui.screens.WalkAmongNumbersIncaRoadsScreen
import com.historytracers.app.ui.screens.WalkAmongNumbersIntroScreen
import com.historytracers.app.ui.screens.WalkAmongNumbersQuestionScreen
import com.historytracers.app.ui.screens.WalkAmongNumbersRoadsScreen
import com.historytracers.app.ui.screens.TextOrNumberBorrowingScreen
import com.historytracers.app.ui.screens.TextOrNumberConclusionScreen
import com.historytracers.app.ui.screens.TextOrNumberIntroScreen
import com.historytracers.app.ui.screens.TextOrNumberMemoryScreen
import com.historytracers.app.ui.screens.TextOrNumberOriginScreen
import com.historytracers.app.ui.screens.TextOrNumberThinkingScreen
import com.historytracers.app.ui.screens.TextOrNumberTodayScreen
import com.historytracers.app.ui.screens.TextOrNumberWhatWeSeeScreen
import com.historytracers.app.ui.screens.MissingNumbersConclusionScreen
import com.historytracers.app.ui.screens.MissingNumbersDifferentSymbolsScreen
import com.historytracers.app.ui.screens.MissingNumbersIntroScreen
import com.historytracers.app.ui.screens.MissingNumbersLackOfEvidenceScreen
import com.historytracers.app.ui.screens.MissingNumbersNextNumbersScreen
import com.historytracers.app.ui.screens.MissingNumbersNumberTenScreen
import com.historytracers.app.ui.screens.MissingNumbersOneByOneScreen
import com.historytracers.app.ui.screens.MissingNumbersThinkingScreen
import com.historytracers.app.ui.screens.IPreferThisConclusionScreen
import com.historytracers.app.ui.screens.IPreferThisIntroScreen
import com.historytracers.app.ui.screens.IPreferThisNumberBeforeScreen
import com.historytracers.app.ui.screens.IPreferThisNumberFourScreen
import com.historytracers.app.ui.screens.IPreferThisNumberNineScreen
import com.historytracers.app.ui.screens.IPreferThisOrientationScreen
import com.historytracers.app.ui.screens.IPreferThisThinkingScreen
import com.historytracers.app.notification.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val language by preferences.language.collectAsState(initial = "en-US")
    val breakTime by preferences.breakTime.collectAsState(initial = 15)
    val skinColor by preferences.skinColor.collectAsState(initial = "#A5672C")
    val calendar by preferences.calendar.collectAsState(initial = "gregorian")
    val scope = rememberCoroutineScope()
    val simpleRoutes = setOf("index", "running_and_growing", "i_am_not_like_you", "equality_intro", "equality_question", "equality_equal", "equality_expanding", "equality_in_general", "equality_conclusion", "totally_equal_intro", "totally_equal_question", "totally_equal_sign", "totally_equal_exercise", "totally_equal_conclusion", "historical_equality_intro", "historical_equality_mapping", "historical_equality_objects", "historical_equality_question", "historical_equality_evidence", "historical_equality_conclusion", "historical_equality_pyramids_intro", "historical_equality_pyramids_definition", "historical_equality_pyramids_mesoamerica", "historical_equality_pyramids_question", "historical_equality_pyramids_look_alike", "historical_equality_pyramids_precision", "historical_equality_pyramids_conclusion", "equal_same_group_different", "first_steps", "sequence_game", "sequence_game_orders", "sequence_game_families", "building_game", "socrates", "socrates_question", "socrates_motivation", "socrates_conclusion", "learning_in_layers_intro", "learning_in_layers_toy", "learning_in_layers_stages", "learning_in_layers_question", "learning_in_layers_playing", "learning_in_layers_conclusion", "how_do_i_learn_intro", "how_do_i_learn_comparisons", "how_do_i_learn_question", "how_do_i_learn_horizon", "how_do_i_learn_chart", "how_do_i_learn_chart_understanding", "how_do_i_learn_decision", "my_hands", "my_hands_question", "my_hands_counting", "my_hands_fingers", "my_hands_conclusion", "my_body", "my_body_feet_question", "my_body_feet", "my_body_everything_question", "my_body_improvement", "my_body_conclusion", "first_hands", "first_hands_question", "first_hands_knowledge", "first_hands_habilis", "first_hands_reflection", "first_hands_counting", "first_hands_conclusion", "first_voice", "first_voice_voice", "first_voice_reflection", "first_voice_difference", "first_voice_conclusion", "drawing_and_couting_intro", "drawing_and_couting_circles", "drawing_and_couting_rectangles", "drawing_and_couting_mesoamericans", "drawing_and_couting_question", "drawing_and_couting_examples", "drawing_and_couting_conclusion", "numbers_intro", "numbers_origin", "numbers_question", "numbers_equal", "numbers_visualizing", "numbers_conclusion", "family_part1_intro", "family_part1_orders", "family_part1_order", "family_part1_altar", "family_part1_continuity", "family_part1_stages", "family_part1_rule", "family_part1_conclusion", "natural_families_part2_intro", "natural_families_part2_question", "natural_families_part2_logic", "natural_families_part2_orders_question", "natural_families_part2_copan", "natural_families_part2_naming", "natural_families_part2_billion_question", "natural_families_part2_conclusion", "the_zero_intro", "the_zero_what_is", "the_zero_intuitive", "the_zero_number", "the_zero_question", "the_zero_conclusion", "workout", "road_to_somewhere", "walk_among_numbers_intro", "walk_among_numbers_roads", "walk_among_numbers_inca_roads", "walk_among_numbers_axes", "walk_among_numbers_hands", "walk_among_numbers_question", "walk_among_numbers_conclusion", "carrying_in_addition_intro", "carrying_in_addition_adding", "carrying_in_addition_hidden_zero", "carrying_in_addition_question", "carrying_in_addition_identity", "carrying_in_addition_zero_in_hands", "carrying_in_addition_conclusion", "adding_same_numbers_intro", "adding_same_numbers_body", "adding_same_numbers_think", "adding_same_numbers_count_one", "adding_same_numbers_count_five", "adding_same_numbers_question", "adding_same_numbers_new_sign", "adding_same_numbers_conclusion", "the_result_is", "inversion_intro", "inversion_squares_going_up", "inversion_squares_lying_down", "inversion_question", "inversion_order", "inversion_conclusion", "order_of_addition_intro", "order_of_addition_where", "order_of_addition_commutative", "order_of_addition_question", "order_of_addition_conclusion", "playing_with_axioms_game", "running_among_numbers_intro", "running_among_numbers_temple", "running_among_numbers_reconstruction", "running_among_numbers_adding", "running_among_numbers_question", "running_among_numbers_conclusion", "abacus", "yupana", "settings", "about", "is_it_free", "streak", "clap", "feet_and_hands", "congratulation", "exercising_addition", "soroban_writing", "suanpan_writing", Screen.SchyotyWriting.route, "large_numbers_writing", "abacus_history_intro", "abacus_history_suanpan", "abacus_history_soroban", "abacus_history_thinking", "abacus_history_similarities", "abacus_history_schyoty", "abacus_history_calculi", "abacus_history_conclusion", "practicing_addition", "practicing_addition_road", "multiplication_table", "multiplying_with_abacus", "multiplying_with_abacus_level2", "multiplying_without_limits", "carrying", "subtracting_with_abacus", "adding_with_abacus", "complement_to_ten", "adding_large_numbers", "relationship", "exercising_multiplication_l2", "practicing_addition_yupana", "practicing_multiplication_yupana", "hands_on_yupana", "drawing_to_count", "iskay_movement", "kimsa_movement", "pisqa_movement", "pichana_movement", "kinkin_movement", "toward_infinity_maximum", "toward_infinity_law", "toward_infinity_without_limits", "toward_infinity_symbol", "toward_infinity_direction", "toward_infinity_conclusion", "limits_min_max_between_both", "limits_min_max_question", "limits_min_max_tending", "limits_min_max_hands", "limits_min_max_conclusion", "where_are_they_intro", "where_are_they_in_us", "where_are_they_in_texts", "where_are_they_ages", "where_are_they_question", "where_are_they_wrong", "where_are_they_species", "where_are_they_universe", "where_are_they_conclusion", "where_are_we_from", "shared_origin_intro", "shared_origin_expanding", "shared_origin_think", "shared_origin_history", "shared_origin_cmb",            "shared_origin_contract", "shared_origin_conclusion", "matter_and_energy_intro",
           "matter_and_energy_transformation", "matter_and_energy_law", "matter_and_energy_question",
           "matter_and_energy_energy", "matter_and_energy_together", "matter_and_energy_conclusion",
           "sharing_with_whom_intro", "sharing_with_whom_nobody", "sharing_with_whom_think",
           "sharing_with_whom_scenario", "sharing_with_whom_empty_region", "sharing_with_whom_conclusion",
           "quipus_intro", "quipus_one_of_the_first", "quipus_how_it_works", "quipus_small_numbers",
           "quipus_large_numbers", "quipus_question", "quipus_conclusion",
           "practicing_with_quipus",
           "quipu_on_the_yupana",
           "mesoamerican_symbols_intro", "mesoamerican_symbols_system", "mesoamerican_symbols_three",
           "mesoamerican_symbols_zero", "mesoamerican_symbols_write", "mesoamerican_symbols_thinking",
           "mesoamerican_symbols_vertical", "mesoamerican_symbols_conclusion",
           "overcoming_limits_intro", "overcoming_limits_previous", "overcoming_limits_next_five",
           "overcoming_limits_logic", "overcoming_limits_last_foot", "overcoming_limits_conclusion",
           "large_numbers_intro", "large_numbers_quipu", "large_numbers_growing",
           "large_numbers_pattern", "large_numbers_app", "large_numbers_conclusion",
           "counting_with_bones_intro", "counting_with_bones_game", "counting_with_bones_practice", "universe_expansion", "another_way_to_count",
           Screen.BuildingLikeAMesoamerican.route, Screen.BuildingLikeEtruscanRomans.route,
           Screen.MesoamericanOrdersIntro.route, Screen.MesoamericanOrdersFirstValue.route,
           Screen.MesoamericanOrdersLastValue.route, Screen.MesoamericanOrdersAncientCalendar.route,
           Screen.MesoamericanOrdersDifferent.route, Screen.MesoamericanOrdersNextOrders.route,
           Screen.MesoamericanOrdersThinking.route, Screen.MesoamericanOrdersNotLikeThis.route,
           Screen.MesoamericanOrdersConclusion.route,
           Screen.TextOrNumberIntro.route, Screen.TextOrNumberMemory.route,
           Screen.TextOrNumberToday.route, Screen.TextOrNumberOrigin.route,
           Screen.TextOrNumberWhatWeSee.route, Screen.TextOrNumberThinking.route,
           Screen.TextOrNumberBorrowing.route, Screen.TextOrNumberConclusion.route,
           Screen.MissingNumbersIntro.route, Screen.MissingNumbersOneByOne.route,
           Screen.MissingNumbersDifferentSymbols.route, Screen.MissingNumbersNextNumbers.route,
           Screen.MissingNumbersThinking.route, Screen.MissingNumbersLackOfEvidence.route,
           Screen.MissingNumbersNumberTen.route, Screen.MissingNumbersConclusion.route,
           Screen.IPreferThisIntro.route, Screen.IPreferThisNumberBefore.route,
           Screen.IPreferThisNumberFour.route, Screen.IPreferThisNumberNine.route,
           Screen.IPreferThisThinking.route, Screen.IPreferThisOrientation.route,
           Screen.IPreferThisConclusion.route,
           Screen.EtruscanRomanTensIntro.route, Screen.EtruscanRomanTensLikeBefore.route,
           Screen.EtruscanRomanTensBetween.route, Screen.EtruscanRomanTensNothingChanges.route,
            Screen.EtruscanRomanTensThinking.route, Screen.EtruscanRomanTensTenNotOne.route,
            Screen.EtruscanRomanTensAndNow.route, Screen.EtruscanRomanTensConclusion.route,
            Screen.HundredsAndThousandsIntro.route, Screen.HundredsAndThousandsAppend.route,
            Screen.HundredsAndThousandsNewOrders.route, Screen.HundredsAndThousandsThinking.route,
            Screen.HundredsAndThousandsContinue.route, Screen.HundredsAndThousandsModify.route,
              Screen.HundredsAndThousandsNewOldLogic.route, Screen.HundredsAndThousandsConclusion.route,
               Screen.IRepresentYou.route,
               Screen.MultiplyingWithYupanaIntro.route, Screen.MultiplyingWithYupanaFirstValues.route,
               Screen.MultiplyingWithYupanaNextStep.route, Screen.MultiplyingWithYupanaOrganizing.route,
               Screen.MultiplyingWithYupanaThinking.route, Screen.MultiplyingWithYupanaProcess.route,
               Screen.MultiplyingWithYupanaConclusion.route)
    val onboardingRoutes = setOf(Screen.Welcome.route, Screen.OnboardingConfig.route)
    var startDest by remember { mutableStateOf<String?>(null) }
    var savedScore by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        val onboarded = preferences.onboardingCompleted.first()
        if (!onboarded) {
            preferences.initDefaultLanguage()
        }
        preferences.lastRoute.first().let { saved ->
            startDest = when {
                !onboarded -> Screen.Welcome.route
                saved in simpleRoutes -> saved
                else -> Screen.Index.route
            }
        }
        savedScore = preferences.score.first()
    }

    if (startDest == null || savedScore == null) return

    var counter by remember { mutableStateOf(savedScore!!) }

    LaunchedEffect(counter) {
        preferences.setScore(counter)
    }

    var breakStartTime by remember { mutableStateOf(System.currentTimeMillis() / 1000L) }
    var showBreakDialog by remember { mutableStateOf(false) }

    LaunchedEffect(breakStartTime, breakTime) {
        if (breakStartTime == 0L) return@LaunchedEffect
        while (true) {
            delay(1000)
            if ((System.currentTimeMillis() / 1000L) - breakStartTime >= breakTime * 60L) {
                showBreakDialog = true
                break
            }
        }
    }

    val streakCount by preferences.streakCount.collectAsState(initial = 0)
    val completedDates by preferences.completedDates.collectAsState(initial = emptySet())
    val streakDays by preferences.streakDays.collectAsState(initial = emptySet())
    val reminderEnabled by preferences.reminderEnabled.collectAsState(initial = true)
    val reminderHour by preferences.reminderHour.collectAsState(initial = 18)
    val reminderMinute by preferences.reminderMinute.collectAsState(initial = 0)

    val uiStrings = uiStringsForLanguage(language)
    val hts = hubTitleStringsForLanguage(language)
    val latestAdditionStrings = latestAdditionScreenStringsForLanguage(language)

    val startDestination = startDest!!

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val navigateFromDrawer: (String) -> Unit = { route ->
        navController.navigate(route) { launchSingleTop = true }
    }

    val savedFirstStepsScroll by preferences.firstStepsScroll.collectAsState(initial = 0)
    val savedWorkoutScroll by preferences.workoutScroll.collectAsState(initial = 0)
    val savedAbacusScroll by preferences.abacusScroll.collectAsState(initial = 0)
    val savedYupanaScroll by preferences.yupanaScroll.collectAsState(initial = 0)
    val savedAnotherWayToCountScroll by preferences.anotherWayToCountScroll.collectAsState(initial = 0)

    val firstStepsScrollState = remember { ScrollState(0) }
    val workoutScrollState = remember { ScrollState(0) }
    val abacusScrollState = remember { ScrollState(0) }
    val yupanaScrollState = remember { ScrollState(0) }
    val anotherWayToCountScrollState = remember { ScrollState(0) }

    LaunchedEffect(savedFirstStepsScroll) {
        if (savedFirstStepsScroll > 0) firstStepsScrollState.scrollTo(savedFirstStepsScroll)
    }
    LaunchedEffect(savedWorkoutScroll) {
        if (savedWorkoutScroll > 0) workoutScrollState.scrollTo(savedWorkoutScroll)
    }
    LaunchedEffect(savedAbacusScroll) {
        if (savedAbacusScroll > 0) abacusScrollState.scrollTo(savedAbacusScroll)
    }
    LaunchedEffect(savedYupanaScroll) {
        if (savedYupanaScroll > 0) yupanaScrollState.scrollTo(savedYupanaScroll)
    }
    LaunchedEffect(savedAnotherWayToCountScroll) {
        if (savedAnotherWayToCountScroll > 0) anotherWayToCountScrollState.scrollTo(savedAnotherWayToCountScroll)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { firstStepsScrollState.value }
            .collect { preferences.setFirstStepsScroll(it) }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { workoutScrollState.value }
            .collect { preferences.setWorkoutScroll(it) }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { abacusScrollState.value }
            .collect { preferences.setAbacusScroll(it) }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { yupanaScrollState.value }
            .collect { preferences.setYupanaScroll(it) }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { anotherWayToCountScrollState.value }
            .collect { preferences.setAnotherWayToCountScroll(it) }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        val route = currentRoute
        if (route != null && route in simpleRoutes) {
            preferences.setLastRoute(route)
        }
    }

    LaunchedEffect(reminderEnabled, reminderHour, reminderMinute, language) {
        NotificationHelper.scheduleAlarm(
            context, reminderEnabled, reminderHour, reminderMinute,
            uiStrings.common.reminderTitle, uiStrings.common.reminderMessage
        )
    }

    CompositionLocalProvider(
        LocalUiStrings provides uiStrings,
        LocalAppLanguage provides language,
        LocalAppCalendar provides calendar
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = currentRoute !in onboardingRoutes,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.padding(top = 24.dp))
                    Text(
                        text = "History Tracers",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(uiStrings.common.home) },
                        selected = currentRoute == Screen.Index.route,
                        onClick = {
                            if (!navController.popBackStack(Screen.Index.route, false)) {
                                navController.navigate(Screen.Index.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(uiStrings.common.settings) },
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            navigateFromDrawer(Screen.Settings.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.LocalFireDepartment, contentDescription = null) },
                        label = { Text(uiStrings.common.streak) },
                        selected = currentRoute == Screen.Streak.route,
                        onClick = {
                            navigateFromDrawer(Screen.Streak.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.NewReleases, contentDescription = null) },
                        label = { Text(latestAdditionStrings.title) },
                        selected = currentRoute == Screen.LatestAddition.route,
                        onClick = {
                            navigateFromDrawer(Screen.LatestAddition.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    Divider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = null) },
                        label = { Text(hts.isItFree) },
                        selected = currentRoute == Screen.IsItFree.route,
                        onClick = {
                            navigateFromDrawer(Screen.IsItFree.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                        label = { Text(hts.aboutUs) },
                        selected = currentRoute == Screen.About.route,
                        onClick = {
                            navigateFromDrawer(Screen.About.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    Divider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Close, contentDescription = null) },
                        label = { Text(uiStrings.common.close) },
                        selected = false,
                        onClick = {
                            (context as? android.app.Activity)?.finishAndRemoveTask()
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    if (currentRoute !in onboardingRoutes) {
                        TopAppBar(
                            title = {},
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        if (drawerState.isOpen) drawerState.close()
                                        else drawerState.open()
                                    }
                                }) {
                                    Icon(Icons.Default.Menu, contentDescription = uiStrings.common.menu)
                                }
                            },
                            actions = {
                                Text(
                                    text = counter.toString(),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.padding(end = 4.dp))
                                Icon(
                                    Icons.Default.Psychology,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.padding(end = 8.dp))
                                Text(
                                    text = streakCount.toString(),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.padding(end = 4.dp))
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.padding(end = 12.dp))
                            }
                        )
                    }
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(padding)
                ) {
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onNavigateNext = { navController.navigate(Screen.OnboardingConfig.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.OnboardingConfig.route) {
                        SettingsScreen(
                            currentLanguage = language,
                            currentBreakTime = breakTime,
                            currentSkinColor = skinColor,
                            currentCalendar = calendar,
                            onLanguageChanged = { lang ->
                                scope.launch { preferences.setLanguage(lang) }
                            },
                            onBreakTimeChanged = { minutes ->
                                scope.launch { preferences.setBreakTime(minutes) }
                            },
                            onSkinColorChanged = { color ->
                                scope.launch { preferences.setSkinColor(color) }
                            },
                            onCalendarChanged = { cal ->
                                scope.launch { preferences.setCalendar(cal) }
                            },
                            onNavigateBack = { navController.popBackStack() },
                            onStartLearning = {
                                scope.launch {
                                    preferences.setOnboardingCompleted()
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Index.route) {
                        IndexScreen(
                            onNavigateToFirstSteps = { navController.navigate(Screen.FirstSteps.route) { launchSingleTop = true } },
                            onNavigateToIAmNotLikeYou = { navController.navigate(Screen.IAmNotLikeYou.route) { launchSingleTop = true } },
                            onNavigateToWorkout = { navController.navigate(Screen.Workout.route) { launchSingleTop = true } },
                            onNavigateToAbacus = { navController.navigate(Screen.Abacus.route) { launchSingleTop = true } },
                            onNavigateToYupana = { navController.navigate(Screen.Yupana.route) { launchSingleTop = true } },
                            onNavigateToRoadToSomewhere = { navController.navigate(Screen.RoadToSomewhere.route) { launchSingleTop = true } },
                            onNavigateToRunningAndGrowing = { navController.navigate(Screen.RunningAndGrowing.route) { launchSingleTop = true } },
                            onNavigateToWhereAreWeFrom = { navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true } },
                            onNavigateToAnotherWayToCount = { navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.WhereAreWeFrom.route) {
                        WhereAreWeFromScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToSharedOrigin = { navController.navigate(Screen.SharedOriginIntro.route) { launchSingleTop = true } },
                            onNavigateToMatterAndEnergy = { navController.navigate(Screen.MatterAndEnergyIntro.route) { launchSingleTop = true } },
                            onNavigateToEverythingWasTogether = { navController.navigate(Screen.UniverseExpansion.route) { launchSingleTop = true } },
                            onNavigateToSharingWithWhom = { navController.navigate(Screen.SharingWithWhomIntro.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AnotherWayToCount.route) {
                        AnotherWayToCountScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            scrollState = anotherWayToCountScrollState,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) },
                            onNavigateToQuipus = { navController.navigate(Screen.QuipusIntro.route) { launchSingleTop = true } },
                            onNavigateToPracticingWithQuipus = { navController.navigate(Screen.PracticingWithQuipus.route) { launchSingleTop = true } },
                            onNavigateToMesoamericanSymbols = { navController.navigate(Screen.MesoamericanSymbolsIntro.route) { launchSingleTop = true } },
                            onNavigateToOvercomingLimits = { navController.navigate(Screen.OvercomingLimitsIntro.route) { launchSingleTop = true } },
                            onNavigateToBuildingLikeAMesoamerican = { navController.navigate(Screen.BuildingLikeAMesoamerican.route) { launchSingleTop = true } },
                            onNavigateToMesoamericanOrder = { navController.navigate(Screen.MesoamericanOrdersIntro.route) { launchSingleTop = true } },
                            onNavigateToTextOrNumber = { navController.navigate(Screen.TextOrNumberIntro.route) { launchSingleTop = true } },
                            onNavigateToTheMissingNumbers = { navController.navigate(Screen.MissingNumbersIntro.route) { launchSingleTop = true } },
                            onNavigateToIPreferThis = { navController.navigate(Screen.IPreferThisIntro.route) { launchSingleTop = true } },
                            onNavigateToBuildingLikeEtruscanRomans = { navController.navigate(Screen.BuildingLikeEtruscanRomans.route) { launchSingleTop = true } },
                            onNavigateToTensAndHundreds = { navController.navigate(Screen.EtruscanRomanTensIntro.route) { launchSingleTop = true } },
                            onNavigateToHundredsAndThousands = { navController.navigate(Screen.HundredsAndThousandsIntro.route) { launchSingleTop = true } },
                            onNavigateToRepresentYou = { navController.navigate(Screen.IRepresentYou.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.BuildingLikeAMesoamerican.route) {
                        BuildingLikeAMesoamericanScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.BuildingLikeEtruscanRomans.route) {
                        BuildingLikeEtruscanRomansScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.EtruscanRomanTensIntro.route) {
                        EtruscanRomanTensIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.EtruscanRomanTensLikeBefore.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.EtruscanRomanTensLikeBefore.route) {
                        EtruscanRomanTensLikeBeforeScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EtruscanRomanTensIntro.route, false)) {
                                    navController.navigate(Screen.EtruscanRomanTensIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.EtruscanRomanTensBetween.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.EtruscanRomanTensBetween.route) {
                        EtruscanRomanTensBetweenScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EtruscanRomanTensLikeBefore.route, false)) {
                                    navController.navigate(Screen.EtruscanRomanTensLikeBefore.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.EtruscanRomanTensNothingChanges.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.EtruscanRomanTensNothingChanges.route) {
                        EtruscanRomanTensNothingChangesScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EtruscanRomanTensBetween.route, false)) {
                                    navController.navigate(Screen.EtruscanRomanTensBetween.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.EtruscanRomanTensThinking.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.EtruscanRomanTensThinking.route) {
                        EtruscanRomanTensThinkingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EtruscanRomanTensNothingChanges.route, false)) {
                                    navController.navigate(Screen.EtruscanRomanTensNothingChanges.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.EtruscanRomanTensTenNotOne.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.EtruscanRomanTensTenNotOne.route) {
                        EtruscanRomanTensTenNotOneScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EtruscanRomanTensThinking.route, false)) {
                                    navController.navigate(Screen.EtruscanRomanTensThinking.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.EtruscanRomanTensAndNow.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.EtruscanRomanTensAndNow.route) {
                        EtruscanRomanTensAndNowScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EtruscanRomanTensTenNotOne.route, false)) {
                                    navController.navigate(Screen.EtruscanRomanTensTenNotOne.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.EtruscanRomanTensConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.EtruscanRomanTensConclusion.route) {
                        EtruscanRomanTensConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EtruscanRomanTensAndNow.route, false)) {
                                    navController.navigate(Screen.EtruscanRomanTensAndNow.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToAnotherWayToCount = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.HundredsAndThousandsIntro.route) {
                        HundredsAndThousandsIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HundredsAndThousandsAppend.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.HundredsAndThousandsAppend.route) {
                        HundredsAndThousandsAppendScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HundredsAndThousandsIntro.route, false)) {
                                    navController.navigate(Screen.HundredsAndThousandsIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HundredsAndThousandsNewOrders.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.HundredsAndThousandsNewOrders.route) {
                        HundredsAndThousandsNewOrdersScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HundredsAndThousandsAppend.route, false)) {
                                    navController.navigate(Screen.HundredsAndThousandsAppend.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HundredsAndThousandsThinking.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.HundredsAndThousandsThinking.route) {
                        HundredsAndThousandsThinkingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HundredsAndThousandsNewOrders.route, false)) {
                                    navController.navigate(Screen.HundredsAndThousandsNewOrders.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HundredsAndThousandsContinue.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.HundredsAndThousandsContinue.route) {
                        HundredsAndThousandsContinueScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HundredsAndThousandsThinking.route, false)) {
                                    navController.navigate(Screen.HundredsAndThousandsThinking.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HundredsAndThousandsModify.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.HundredsAndThousandsModify.route) {
                        HundredsAndThousandsModifyScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HundredsAndThousandsContinue.route, false)) {
                                    navController.navigate(Screen.HundredsAndThousandsContinue.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HundredsAndThousandsNewOldLogic.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.HundredsAndThousandsNewOldLogic.route) {
                        HundredsAndThousandsNewOldLogicScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HundredsAndThousandsModify.route, false)) {
                                    navController.navigate(Screen.HundredsAndThousandsModify.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HundredsAndThousandsConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.HundredsAndThousandsConclusion.route) {
                        HundredsAndThousandsConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HundredsAndThousandsNewOldLogic.route, false)) {
                                    navController.navigate(Screen.HundredsAndThousandsNewOldLogic.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToAnotherWayToCount = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.IRepresentYou.route) {
                        RepresentYouScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.MesoamericanOrdersIntro.route) {
                        MesoamericanOrdersIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanOrdersFirstValue.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanOrdersFirstValue.route) {
                        MesoamericanOrdersFirstValueScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanOrdersIntro.route, false)) {
                                    navController.navigate(Screen.MesoamericanOrdersIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanOrdersLastValue.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanOrdersLastValue.route) {
                        MesoamericanOrdersLastValueScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanOrdersFirstValue.route, false)) {
                                    navController.navigate(Screen.MesoamericanOrdersFirstValue.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanOrdersAncientCalendar.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanOrdersAncientCalendar.route) {
                        MesoamericanOrdersAncientCalendarScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanOrdersLastValue.route, false)) {
                                    navController.navigate(Screen.MesoamericanOrdersLastValue.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanOrdersDifferent.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanOrdersDifferent.route) {
                        MesoamericanOrdersDifferentIsNotWrongScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanOrdersAncientCalendar.route, false)) {
                                    navController.navigate(Screen.MesoamericanOrdersAncientCalendar.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanOrdersNextOrders.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanOrdersNextOrders.route) {
                        MesoamericanOrdersNextOrdersScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanOrdersDifferent.route, false)) {
                                    navController.navigate(Screen.MesoamericanOrdersDifferent.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanOrdersThinking.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanOrdersThinking.route) {
                        MesoamericanOrdersThinkingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanOrdersNextOrders.route, false)) {
                                    navController.navigate(Screen.MesoamericanOrdersNextOrders.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanOrdersNotLikeThis.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanOrdersNotLikeThis.route) {
                        MesoamericanOrdersItIsNotLikeThisScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanOrdersThinking.route, false)) {
                                    navController.navigate(Screen.MesoamericanOrdersThinking.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanOrdersConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanOrdersConclusion.route) {
                        MesoamericanOrdersConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanOrdersNotLikeThis.route, false)) {
                                    navController.navigate(Screen.MesoamericanOrdersNotLikeThis.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToAnotherWayToCount = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.TextOrNumberIntro.route) {
                        TextOrNumberIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.TextOrNumberMemory.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.TextOrNumberMemory.route) {
                        TextOrNumberMemoryScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TextOrNumberIntro.route, false)) {
                                    navController.navigate(Screen.TextOrNumberIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.TextOrNumberToday.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.TextOrNumberToday.route) {
                        TextOrNumberTodayScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TextOrNumberMemory.route, false)) {
                                    navController.navigate(Screen.TextOrNumberMemory.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.TextOrNumberOrigin.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.TextOrNumberOrigin.route) {
                        TextOrNumberOriginScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TextOrNumberToday.route, false)) {
                                    navController.navigate(Screen.TextOrNumberToday.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.TextOrNumberWhatWeSee.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.TextOrNumberWhatWeSee.route) {
                        TextOrNumberWhatWeSeeScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TextOrNumberOrigin.route, false)) {
                                    navController.navigate(Screen.TextOrNumberOrigin.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.TextOrNumberThinking.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.TextOrNumberThinking.route) {
                        TextOrNumberThinkingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TextOrNumberWhatWeSee.route, false)) {
                                    navController.navigate(Screen.TextOrNumberWhatWeSee.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.TextOrNumberBorrowing.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.TextOrNumberBorrowing.route) {
                        TextOrNumberBorrowingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TextOrNumberThinking.route, false)) {
                                    navController.navigate(Screen.TextOrNumberThinking.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.TextOrNumberConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.TextOrNumberConclusion.route) {
                        TextOrNumberConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TextOrNumberBorrowing.route, false)) {
                                    navController.navigate(Screen.TextOrNumberBorrowing.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToAnotherWayToCount = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.MissingNumbersIntro.route) {
                        MissingNumbersIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MissingNumbersOneByOne.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MissingNumbersOneByOne.route) {
                        MissingNumbersOneByOneScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MissingNumbersIntro.route, false)) {
                                    navController.navigate(Screen.MissingNumbersIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MissingNumbersDifferentSymbols.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MissingNumbersDifferentSymbols.route) {
                        MissingNumbersDifferentSymbolsScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MissingNumbersOneByOne.route, false)) {
                                    navController.navigate(Screen.MissingNumbersOneByOne.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MissingNumbersNextNumbers.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MissingNumbersNextNumbers.route) {
                        MissingNumbersNextNumbersScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MissingNumbersDifferentSymbols.route, false)) {
                                    navController.navigate(Screen.MissingNumbersDifferentSymbols.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MissingNumbersThinking.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MissingNumbersThinking.route) {
                        MissingNumbersThinkingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MissingNumbersNextNumbers.route, false)) {
                                    navController.navigate(Screen.MissingNumbersNextNumbers.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MissingNumbersLackOfEvidence.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MissingNumbersLackOfEvidence.route) {
                        MissingNumbersLackOfEvidenceScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MissingNumbersThinking.route, false)) {
                                    navController.navigate(Screen.MissingNumbersThinking.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MissingNumbersNumberTen.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MissingNumbersNumberTen.route) {
                        MissingNumbersNumberTenScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MissingNumbersLackOfEvidence.route, false)) {
                                    navController.navigate(Screen.MissingNumbersLackOfEvidence.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MissingNumbersConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MissingNumbersConclusion.route) {
                        MissingNumbersConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MissingNumbersNumberTen.route, false)) {
                                    navController.navigate(Screen.MissingNumbersNumberTen.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToAnotherWayToCount = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.IPreferThisIntro.route) {
                        IPreferThisIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.IPreferThisNumberBefore.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.IPreferThisNumberBefore.route) {
                        IPreferThisNumberBeforeScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.IPreferThisIntro.route, false)) {
                                    navController.navigate(Screen.IPreferThisIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.IPreferThisNumberFour.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.IPreferThisNumberFour.route) {
                        IPreferThisNumberFourScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.IPreferThisNumberBefore.route, false)) {
                                    navController.navigate(Screen.IPreferThisNumberBefore.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.IPreferThisNumberNine.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.IPreferThisNumberNine.route) {
                        IPreferThisNumberNineScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.IPreferThisNumberFour.route, false)) {
                                    navController.navigate(Screen.IPreferThisNumberFour.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.IPreferThisThinking.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.IPreferThisThinking.route) {
                        IPreferThisThinkingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.IPreferThisNumberNine.route, false)) {
                                    navController.navigate(Screen.IPreferThisNumberNine.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.IPreferThisOrientation.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.IPreferThisOrientation.route) {
                        IPreferThisOrientationScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.IPreferThisThinking.route, false)) {
                                    navController.navigate(Screen.IPreferThisThinking.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.IPreferThisConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.IPreferThisConclusion.route) {
                        IPreferThisConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.IPreferThisOrientation.route, false)) {
                                    navController.navigate(Screen.IPreferThisOrientation.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToAnotherWayToCount = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.QuipusIntro.route) {
                        QuipusIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.QuipusOneOfTheFirst.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.QuipusOneOfTheFirst.route) {
                        QuipusOneOfTheFirstScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.QuipusIntro.route, false)) {
                                    navController.navigate(Screen.QuipusIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.QuipusHowItWorks.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.QuipusHowItWorks.route) {
                        QuipusHowItWorksScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.QuipusOneOfTheFirst.route, false)) {
                                    navController.navigate(Screen.QuipusOneOfTheFirst.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.QuipusSmallNumbers.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.QuipusSmallNumbers.route) {
                        QuipusSmallNumbersScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.QuipusHowItWorks.route, false)) {
                                    navController.navigate(Screen.QuipusHowItWorks.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.QuipusLargeNumbers.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.QuipusLargeNumbers.route) {
                        QuipusLargeNumbersScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.QuipusSmallNumbers.route, false)) {
                                    navController.navigate(Screen.QuipusSmallNumbers.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.QuipusQuestion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.QuipusQuestion.route) {
                        QuipusQuestionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.QuipusLargeNumbers.route, false)) {
                                    navController.navigate(Screen.QuipusLargeNumbers.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.QuipusConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.QuipusConclusion.route) {
                        QuipusConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.QuipusQuestion.route, false)) {
                                    navController.navigate(Screen.QuipusQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToAnotherWayToCount = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.PracticingWithQuipus.route) {
                        PracticingWithQuipusScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            restoreStep = navController.previousBackStackEntry == null,
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.UniverseExpansion.route) {
                        UniverseExpansionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            restoreStep = navController.previousBackStackEntry == null,
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToWhereAreWeFrom = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.SharedOriginIntro.route) {
                        SharedOriginIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharedOriginExpanding.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharedOriginExpanding.route) {
                        SharedOriginExpandingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharedOriginIntro.route, false)) {
                                    navController.navigate(Screen.SharedOriginIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharedOriginThink.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharedOriginThink.route) {
                        SharedOriginThinkScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharedOriginExpanding.route, false)) {
                                    navController.navigate(Screen.SharedOriginExpanding.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharedOriginHistory.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharedOriginHistory.route) {
                        SharedOriginHistoryScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharedOriginThink.route, false)) {
                                    navController.navigate(Screen.SharedOriginThink.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharedOriginCmb.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharedOriginCmb.route) {
                        SharedOriginCmbScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharedOriginHistory.route, false)) {
                                    navController.navigate(Screen.SharedOriginHistory.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharedOriginContract.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharedOriginContract.route) {
                        SharedOriginContractScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharedOriginCmb.route, false)) {
                                    navController.navigate(Screen.SharedOriginCmb.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharedOriginConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharedOriginConclusion.route) {
                        SharedOriginConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharedOriginContract.route, false)) {
                                    navController.navigate(Screen.SharedOriginContract.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToWhereAreWeFrom = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.MatterAndEnergyIntro.route) {
                        MatterAndEnergyIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MatterAndEnergyTransformation.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MatterAndEnergyTransformation.route) {
                        MatterAndEnergyTransformationScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MatterAndEnergyIntro.route, false)) {
                                    navController.navigate(Screen.MatterAndEnergyIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MatterAndEnergyLaw.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MatterAndEnergyLaw.route) {
                        MatterAndEnergyLawScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MatterAndEnergyTransformation.route, false)) {
                                    navController.navigate(Screen.MatterAndEnergyTransformation.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MatterAndEnergyQuestion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MatterAndEnergyQuestion.route) {
                        MatterAndEnergyQuestionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MatterAndEnergyLaw.route, false)) {
                                    navController.navigate(Screen.MatterAndEnergyLaw.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MatterAndEnergyEnergy.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MatterAndEnergyEnergy.route) {
                        MatterAndEnergyEnergyScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MatterAndEnergyQuestion.route, false)) {
                                    navController.navigate(Screen.MatterAndEnergyQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MatterAndEnergyTogether.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MatterAndEnergyTogether.route) {
                        MatterAndEnergyTogetherScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MatterAndEnergyEnergy.route, false)) {
                                    navController.navigate(Screen.MatterAndEnergyEnergy.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MatterAndEnergyConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MatterAndEnergyConclusion.route) {
                        MatterAndEnergyConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MatterAndEnergyTogether.route, false)) {
                                    navController.navigate(Screen.MatterAndEnergyTogether.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToWhereAreWeFrom = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.SharingWithWhomIntro.route) {
                        SharingWithWhomIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharingWithWhomNobody.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharingWithWhomNobody.route) {
                        SharingWithWhomNobodyScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharingWithWhomIntro.route, false)) {
                                    navController.navigate(Screen.SharingWithWhomIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharingWithWhomThink.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharingWithWhomThink.route) {
                        SharingWithWhomThinkScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharingWithWhomNobody.route, false)) {
                                    navController.navigate(Screen.SharingWithWhomNobody.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharingWithWhomScenario.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharingWithWhomScenario.route) {
                        SharingWithWhomScenarioScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharingWithWhomThink.route, false)) {
                                    navController.navigate(Screen.SharingWithWhomThink.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharingWithWhomEmptyRegion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharingWithWhomEmptyRegion.route) {
                        SharingWithWhomEmptyRegionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharingWithWhomScenario.route, false)) {
                                    navController.navigate(Screen.SharingWithWhomScenario.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SharingWithWhomConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SharingWithWhomConclusion.route) {
                        SharingWithWhomConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.SharingWithWhomEmptyRegion.route, false)) {
                                    navController.navigate(Screen.SharingWithWhomEmptyRegion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToWhereAreWeFrom = {
                                if (!navController.popBackStack(Screen.WhereAreWeFrom.route, false)) {
                                    navController.navigate(Screen.WhereAreWeFrom.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.CountingWithBonesIntro.route) {
                        CountingWithBonesIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.CountingWithBonesGame.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.CountingWithBonesGame.route) {
                        CountingWithBonesGameScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.CountingWithBonesIntro.route, false)) {
                                    navController.navigate(Screen.CountingWithBonesIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.CountingWithBonesPractice.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.CountingWithBonesPractice.route) {
                        CountingWithBonesPracticeScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.CountingWithBonesGame.route, false)) {
                                    navController.navigate(Screen.CountingWithBonesGame.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.RunningAndGrowing.route) {
                        RunningAndGrowingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) { launchSingleTop = true } },
                            onNavigateToAddingTheSameNumber = { navController.navigate(Screen.AddingSameNumbersIntro.route) { launchSingleTop = true } },
                            onNavigateToTheResultIs = { navController.navigate(Screen.TheResultIs.route) { launchSingleTop = true } },
                            onNavigateToInversion = { navController.navigate(Screen.InversionIntro.route) { launchSingleTop = true } },
                            onNavigateToConnectingTheMultiplication = { navController.navigate(Screen.DrawingTheTableOfZeroIntro.route) { launchSingleTop = true } },
                            onNavigateToDrawingMultiplication = { navController.navigate(Screen.DrawingTheTableOfOneIntro.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.RoadToSomewhere.route) {
                        RoadToSomewhereScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToWalkAmongNumbers = { navController.navigate(Screen.WalkAmongNumbersIntro.route) { launchSingleTop = true } },
                            onNavigateToCarryingInAddition = { navController.navigate(Screen.CarryingInAdditionIntro.route) { launchSingleTop = true } },
                            onNavigateToOrderOfAddition = { navController.navigate(Screen.OrderOfAdditionIntro.route) { launchSingleTop = true } },
                            onNavigateToPlayingWithAxioms = { navController.navigate(Screen.PlayingWithAxiomsGame.route) { launchSingleTop = true } },
                            onNavigateToRunningAmongNumbers = { navController.navigate(Screen.RunningAmongNumbersIntro.route) { launchSingleTop = true } },
                            onNavigateToPracticingAddition = { navController.navigate(Screen.PracticingAdditionRoad.route) { launchSingleTop = true } },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.PracticingAdditionRoad.route) {
                        PracticingAdditionRoadScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.WalkAmongNumbersIntro.route) {
                        WalkAmongNumbersIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.WalkAmongNumbersRoads.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.WalkAmongNumbersRoads.route) {
                        WalkAmongNumbersRoadsScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersIntro.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersIncaRoads.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersIncaRoads.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.WalkAmongNumbersIncaRoads.route) {
                        WalkAmongNumbersIncaRoadsScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersRoads.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersRoads.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersAxes.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersAxes.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.WalkAmongNumbersAxes.route) {
                        WalkAmongNumbersAxesScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersIncaRoads.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersIncaRoads.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersHands.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersHands.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.WalkAmongNumbersHands.route) {
                        WalkAmongNumbersHandsScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersAxes.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersAxes.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersQuestion.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersQuestion.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.WalkAmongNumbersQuestion.route) {
                        WalkAmongNumbersQuestionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersHands.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersHands.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersConclusion.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersConclusion.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.WalkAmongNumbersConclusion.route) {
                        WalkAmongNumbersConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.WalkAmongNumbersQuestion.route, false)) {
                                    navController.navigate(Screen.WalkAmongNumbersQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToRoadToSomewhere = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.CarryingInAdditionIntro.route) {
                        CarryingInAdditionIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.CarryingInAdditionAdding.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.CarryingInAdditionAdding.route) {
                        CarryingInAdditionAddingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionIntro.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionHiddenZero.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionHiddenZero.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.CarryingInAdditionHiddenZero.route) {
                        CarryingInAdditionHiddenZeroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionAdding.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionAdding.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionQuestion.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionQuestion.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.CarryingInAdditionQuestion.route) {
                        CarryingInAdditionQuestionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionHiddenZero.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionHiddenZero.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionIdentity.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionIdentity.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.CarryingInAdditionIdentity.route) {
                        CarryingInAdditionIdentityScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionQuestion.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionZeroInHands.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionZeroInHands.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.CarryingInAdditionZeroInHands.route) {
                        CarryingInAdditionZeroInHandsScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionIdentity.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionIdentity.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionConclusion.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionConclusion.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.CarryingInAdditionConclusion.route) {
                        CarryingInAdditionConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.CarryingInAdditionZeroInHands.route, false)) {
                                    navController.navigate(Screen.CarryingInAdditionZeroInHands.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToRoadToSomewhere = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.AddingSameNumbersIntro.route) {
                        AddingSameNumbersIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AddingSameNumbersBody.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AddingSameNumbersBody.route) {
                        AddingSameNumbersBodyScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AddingSameNumbersIntro.route, false)) {
                                    navController.navigate(Screen.AddingSameNumbersIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AddingSameNumbersThink.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AddingSameNumbersThink.route) {
                        AddingSameNumbersThinkScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AddingSameNumbersBody.route, false)) {
                                    navController.navigate(Screen.AddingSameNumbersBody.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AddingSameNumbersCountOne.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AddingSameNumbersCountOne.route) {
                        AddingSameNumbersCountOneScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AddingSameNumbersThink.route, false)) {
                                    navController.navigate(Screen.AddingSameNumbersThink.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AddingSameNumbersCountFive.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AddingSameNumbersCountFive.route) {
                        AddingSameNumbersCountFiveScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AddingSameNumbersCountOne.route, false)) {
                                    navController.navigate(Screen.AddingSameNumbersCountOne.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AddingSameNumbersQuestion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AddingSameNumbersQuestion.route) {
                        AddingSameNumbersQuestionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AddingSameNumbersCountFive.route, false)) {
                                    navController.navigate(Screen.AddingSameNumbersCountFive.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AddingSameNumbersNewSign.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AddingSameNumbersNewSign.route) {
                        AddingSameNumbersNewSignScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AddingSameNumbersQuestion.route, false)) {
                                    navController.navigate(Screen.AddingSameNumbersQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AddingSameNumbersConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AddingSameNumbersConclusion.route) {
                        AddingSameNumbersConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AddingSameNumbersNewSign.route, false)) {
                                    navController.navigate(Screen.AddingSameNumbersNewSign.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToRunningAndGrowing = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.TheResultIs.route) {
                        TheResultIsScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.InversionIntro.route) {
                        InversionIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.InversionSquaresGoingUp.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.InversionSquaresGoingUp.route) {
                        InversionSquaresGoingUpScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.InversionIntro.route, false)) {
                                    navController.navigate(Screen.InversionIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.InversionSquaresLyingDown.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.InversionSquaresLyingDown.route) {
                        InversionSquaresLyingDownScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.InversionSquaresGoingUp.route, false)) {
                                    navController.navigate(Screen.InversionSquaresGoingUp.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.InversionQuestion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.InversionQuestion.route) {
                        InversionQuestionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.InversionSquaresLyingDown.route, false)) {
                                    navController.navigate(Screen.InversionSquaresLyingDown.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.InversionOrder.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.InversionOrder.route) {
                        InversionOrderScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.InversionQuestion.route, false)) {
                                    navController.navigate(Screen.InversionQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.InversionConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.InversionConclusion.route) {
                        InversionConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.InversionOrder.route, false)) {
                                    navController.navigate(Screen.InversionOrder.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToRunningAndGrowing = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.DrawingTheTableOfZeroIntro.route) {
                        DrawingTheTableOfZeroIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfZeroRule.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfZeroRule.route) {
                        DrawingTheTableOfZeroRuleScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfZeroIntro.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfZeroIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfZeroTable.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfZeroTable.route) {
                        DrawingTheTableOfZeroTableScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfZeroRule.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfZeroRule.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfZeroDrawing.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfZeroDrawing.route) {
                        DrawingTheTableOfZeroDrawingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfZeroTable.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfZeroTable.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfZeroQuestion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfZeroQuestion.route) {
                        DrawingTheTableOfZeroQuestionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfZeroDrawing.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfZeroDrawing.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfZeroFlatLine.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfZeroFlatLine.route) {
                        DrawingTheTableOfZeroFlatLineScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfZeroQuestion.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfZeroQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfZeroConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfZeroConclusion.route) {
                        DrawingTheTableOfZeroConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfZeroFlatLine.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfZeroFlatLine.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToRunningAndGrowing = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.DrawingTheTableOfOneIntro.route) {
                        DrawingTheTableOfOneIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfOneRule.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfOneRule.route) {
                        DrawingTheTableOfOneRuleScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfOneIntro.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfOneIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfOneDrawing.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfOneDrawing.route) {
                        DrawingTheTableOfOneDrawingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfOneRule.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfOneRule.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfOneQuestion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfOneQuestion.route) {
                        DrawingTheTableOfOneQuestionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfOneDrawing.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfOneDrawing.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfOneBoxes.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfOneBoxes.route) {
                        DrawingTheTableOfOneBoxesScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfOneQuestion.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfOneQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfOneConnect.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfOneConnect.route) {
                        DrawingTheTableOfOneConnectScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfOneBoxes.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfOneBoxes.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingTheTableOfOneConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingTheTableOfOneConclusion.route) {
                        DrawingTheTableOfOneConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.DrawingTheTableOfOneConnect.route, false)) {
                                    navController.navigate(Screen.DrawingTheTableOfOneConnect.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToRunningAndGrowing = {
                                if (!navController.popBackStack(Screen.RunningAndGrowing.route, false)) {
                                    navController.navigate(Screen.RunningAndGrowing.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.OrderOfAdditionIntro.route) {
                        OrderOfAdditionIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.OrderOfAdditionWhere.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.OrderOfAdditionWhere.route) {
                        OrderOfAdditionWhereScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.OrderOfAdditionIntro.route, false)) {
                                    navController.navigate(Screen.OrderOfAdditionIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.OrderOfAdditionCommutative.route, false)) {
                                    navController.navigate(Screen.OrderOfAdditionCommutative.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.OrderOfAdditionCommutative.route) {
                        OrderOfAdditionCommutativeScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.OrderOfAdditionWhere.route, false)) {
                                    navController.navigate(Screen.OrderOfAdditionWhere.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.OrderOfAdditionQuestion.route, false)) {
                                    navController.navigate(Screen.OrderOfAdditionQuestion.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.OrderOfAdditionQuestion.route) {
                        OrderOfAdditionQuestionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.OrderOfAdditionCommutative.route, false)) {
                                    navController.navigate(Screen.OrderOfAdditionCommutative.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.OrderOfAdditionConclusion.route, false)) {
                                    navController.navigate(Screen.OrderOfAdditionConclusion.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.OrderOfAdditionConclusion.route) {
                        OrderOfAdditionConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.OrderOfAdditionQuestion.route, false)) {
                                    navController.navigate(Screen.OrderOfAdditionQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToRoadToSomewhere = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.PlayingWithAxiomsGame.route) {
                        PlayingWithAxiomsGameScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToRoadToSomewhere = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.RunningAmongNumbersIntro.route) {
                        RunningAmongNumbersIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.RunningAmongNumbersTemple.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.RunningAmongNumbersTemple.route) {
                        RunningAmongNumbersTempleScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.RunningAmongNumbersIntro.route, false)) {
                                    navController.navigate(Screen.RunningAmongNumbersIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.RunningAmongNumbersReconstruction.route, false)) {
                                    navController.navigate(Screen.RunningAmongNumbersReconstruction.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.RunningAmongNumbersReconstruction.route) {
                        RunningAmongNumbersReconstructionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.RunningAmongNumbersTemple.route, false)) {
                                    navController.navigate(Screen.RunningAmongNumbersTemple.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.RunningAmongNumbersAdding.route, false)) {
                                    navController.navigate(Screen.RunningAmongNumbersAdding.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.RunningAmongNumbersAdding.route) {
                        RunningAmongNumbersAddingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.RunningAmongNumbersReconstruction.route, false)) {
                                    navController.navigate(Screen.RunningAmongNumbersReconstruction.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.RunningAmongNumbersQuestion.route, false)) {
                                    navController.navigate(Screen.RunningAmongNumbersQuestion.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.RunningAmongNumbersQuestion.route) {
                        RunningAmongNumbersQuestionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.RunningAmongNumbersAdding.route, false)) {
                                    navController.navigate(Screen.RunningAmongNumbersAdding.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.RunningAmongNumbersConclusion.route, false)) {
                                    navController.navigate(Screen.RunningAmongNumbersConclusion.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.RunningAmongNumbersConclusion.route) {
                        RunningAmongNumbersConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.RunningAmongNumbersQuestion.route, false)) {
                                    navController.navigate(Screen.RunningAmongNumbersQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToRoadToSomewhere = {
                                if (!navController.popBackStack(Screen.RoadToSomewhere.route, false)) {
                                    navController.navigate(Screen.RoadToSomewhere.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.IAmNotLikeYou.route) {
                        IAmNotLikeYouScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToEquality = { navController.navigate(Screen.EqualityIntro.route) { launchSingleTop = true } },
                            onNavigateToTotallyEqual = { navController.navigate(Screen.TotallyEqualIntro.route) { launchSingleTop = true } },
                            onNavigateToHistoricalEquality = { navController.navigate(Screen.HistoricalEqualityIntro.route) { launchSingleTop = true } },
                            onNavigateToHistoricalEqualityPyramids = { navController.navigate(Screen.HistoricalEqualityPyramidsIntro.route) { launchSingleTop = true } },
                            onNavigateToEqualSameGroupDifferent = { navController.navigate(Screen.EqualSameGroupDifferent.route) { launchSingleTop = true } },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.EqualSameGroupDifferent.route) {
                        EqualSameGroupDifferentScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.EqualityIntro.route) {
                        EqualityIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.EqualityQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.EqualityQuestion.route) {
                        EqualityQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EqualityIntro.route, false)) {
                                    navController.navigate(Screen.EqualityIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.EqualityEqual.route, false)) {
                                    navController.navigate(Screen.EqualityEqual.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.EqualityEqual.route) {
                        EqualityEqualScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EqualityQuestion.route, false)) {
                                    navController.navigate(Screen.EqualityQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.EqualityExpanding.route, false)) {
                                    navController.navigate(Screen.EqualityExpanding.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.EqualityExpanding.route) {
                        EqualityExpandingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EqualityEqual.route, false)) {
                                    navController.navigate(Screen.EqualityEqual.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.EqualityInGeneral.route, false)) {
                                    navController.navigate(Screen.EqualityInGeneral.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.EqualityInGeneral.route) {
                        EqualityInGeneralScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EqualityExpanding.route, false)) {
                                    navController.navigate(Screen.EqualityExpanding.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.EqualityConclusion.route, false)) {
                                    navController.navigate(Screen.EqualityConclusion.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.EqualityConclusion.route) {
                        EqualityConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.EqualityInGeneral.route, false)) {
                                    navController.navigate(Screen.EqualityInGeneral.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToIAmNotLikeYou = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TotallyEqualIntro.route) {
                        TotallyEqualIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.TotallyEqualQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TotallyEqualQuestion.route) {
                        TotallyEqualQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TotallyEqualIntro.route, false)) {
                                    navController.navigate(Screen.TotallyEqualIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.TotallyEqualSign.route, false)) {
                                    navController.navigate(Screen.TotallyEqualSign.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TotallyEqualSign.route) {
                        TotallyEqualSignScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TotallyEqualQuestion.route, false)) {
                                    navController.navigate(Screen.TotallyEqualQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.TotallyEqualExercise.route, false)) {
                                    navController.navigate(Screen.TotallyEqualExercise.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TotallyEqualExercise.route) {
                        TotallyEqualExerciseScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TotallyEqualSign.route, false)) {
                                    navController.navigate(Screen.TotallyEqualSign.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.TotallyEqualConclusion.route, false)) {
                                    navController.navigate(Screen.TotallyEqualConclusion.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TotallyEqualConclusion.route) {
                        TotallyEqualConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.TotallyEqualExercise.route, false)) {
                                    navController.navigate(Screen.TotallyEqualExercise.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToIAmNotLikeYou = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityIntro.route) {
                        HistoricalEqualityIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HistoricalEqualityMapping.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityMapping.route) {
                        HistoricalEqualityMappingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityIntro.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityObjects.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityObjects.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityObjects.route) {
                        HistoricalEqualityObjectsScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityMapping.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityMapping.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityQuestion.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityQuestion.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityQuestion.route) {
                        HistoricalEqualityQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityObjects.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityObjects.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityEvidence.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityEvidence.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityEvidence.route) {
                        HistoricalEqualityEvidenceScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityQuestion.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityConclusion.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityConclusion.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityConclusion.route) {
                        HistoricalEqualityConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityEvidence.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityEvidence.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToIAmNotLikeYou = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityPyramidsIntro.route) {
                        HistoricalEqualityPyramidsIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HistoricalEqualityPyramidsDefinition.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityPyramidsDefinition.route) {
                        HistoricalEqualityPyramidsDefinitionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsIntro.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsMesoamerica.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsMesoamerica.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityPyramidsMesoamerica.route) {
                        HistoricalEqualityPyramidsMesoamericaScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsDefinition.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsDefinition.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsQuestion.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsQuestion.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityPyramidsQuestion.route) {
                        HistoricalEqualityPyramidsQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsMesoamerica.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsMesoamerica.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsLookAlike.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsLookAlike.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityPyramidsLookAlike.route) {
                        HistoricalEqualityPyramidsLookAlikeScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsQuestion.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsQuestion.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsPrecision.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsPrecision.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityPyramidsPrecision.route) {
                        HistoricalEqualityPyramidsPrecisionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsLookAlike.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsLookAlike.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsConclusion.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsConclusion.route) { launchSingleTop = true }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HistoricalEqualityPyramidsConclusion.route) {
                        HistoricalEqualityPyramidsConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.HistoricalEqualityPyramidsPrecision.route, false)) {
                                    navController.navigate(Screen.HistoricalEqualityPyramidsPrecision.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToIAmNotLikeYou = {
                                if (!navController.popBackStack(Screen.IAmNotLikeYou.route, false)) {
                                    navController.navigate(Screen.IAmNotLikeYou.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstSteps.route) {
                        FirstStepsScreen(
                            scrollState = firstStepsScrollState,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) },
                            onNavigateToSocrates = { navController.navigate(Screen.Socrates.route) { launchSingleTop = true } },
                            onNavigateToLearningInLayers = { navController.navigate(Screen.LearningInLayersIntro.route) { launchSingleTop = true } },
                            onNavigateToHowDoILearn = { navController.navigate(Screen.HowDoILearnIntro.route) { launchSingleTop = true } },
                            onNavigateToMyHands = { navController.navigate(Screen.MyHands.route) { launchSingleTop = true } },
                            onNavigateToMyBody = { navController.navigate(Screen.MyBody.route) { launchSingleTop = true } },
                            onNavigateToFirstHands = { navController.navigate(Screen.FirstHands.route) { launchSingleTop = true } },
                            onNavigateToCountingWithBones = { navController.navigate(Screen.CountingWithBonesIntro.route) { launchSingleTop = true } },
                            onNavigateToFirstVoice = { navController.navigate(Screen.FirstVoice.route) { launchSingleTop = true } },
                            onNavigateToDrawingAndCouting = { navController.navigate(Screen.DrawingAndCoutingIntro.route) { launchSingleTop = true } },
                            onNavigateToNumbers = { navController.navigate(Screen.NumbersIntro.route) { launchSingleTop = true } },
                            onNavigateToFamilyPart1 = { navController.navigate(Screen.FamilyPart1Intro.route) { launchSingleTop = true } },
                            onNavigateToTheZero = { navController.navigate(Screen.TheZeroIntro.route) { launchSingleTop = true } },
                            onNavigateToSequenceGame = { navController.navigate(Screen.SequenceGame.route) { launchSingleTop = true } },
                            onNavigateToSequenceGameOrders = { navController.navigate(Screen.SequenceGameOrders.route) { launchSingleTop = true } },
                            onNavigateToSequenceGameFamilies = { navController.navigate(Screen.SequenceGameFamilies.route) { launchSingleTop = true } },
                            onNavigateToBuildingGame = { navController.navigate(Screen.BuildingGame.route) { launchSingleTop = true } },
                            onNavigateToNaturalFamiliesPart2 = { navController.navigate(Screen.NaturalFamiliesPart2Intro.route) { launchSingleTop = true } },
                            onNavigateToTowardInfinity = { navController.navigate(Screen.TowardInfinityMaximum.route) { launchSingleTop = true } },
                            onNavigateToLimitsMinMax = { navController.navigate(Screen.LimitsMinMaxBetweenBoth.route) { launchSingleTop = true } },
                            onNavigateToWhereAreThey = { navController.navigate(Screen.WhereAreTheyIntro.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SequenceGameOrders.route) {
                        SequenceGameOrdersScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SequenceGame.route) {
                        SequenceGameScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SequenceGameFamilies.route) {
                        SequenceGameFamiliesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.BuildingGame.route) {
                        BuildingGameScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.Socrates.route) {
                        SocratesPhilosophyScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SocratesQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SocratesQuestion.route) {
                        SocratesQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.Socrates.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.SocratesMotivation.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SocratesMotivation.route) {
                        SocratesMotivationScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.SocratesQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.SocratesConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SocratesConclusion.route) {
                        SocratesConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.SocratesMotivation.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersIntro.route) {
                        LearningInLayersIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.LearningInLayersToy.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersToy.route) {
                        LearningInLayersToyScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LearningInLayersIntro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LearningInLayersStages.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersStages.route) {
                        LearningInLayersStagesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LearningInLayersToy.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LearningInLayersQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersQuestion.route) {
                        LearningInLayersQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LearningInLayersStages.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LearningInLayersPlaying.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersPlaying.route) {
                        LearningInLayersPlayingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LearningInLayersQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LearningInLayersConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersConclusion.route) {
                        LearningInLayersConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LearningInLayersPlaying.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnIntro.route) {
                        HowDoILearnIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnComparisons.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnComparisons.route) {
                        HowDoILearnComparisonsScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnIntro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnQuestion.route) {
                        HowDoILearnQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnComparisons.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnHorizon.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnHorizon.route) {
                        HowDoILearnHorizonScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnChart.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnChart.route) {
                        HowDoILearnChartScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnHorizon.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnChartUnderstanding.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnChartUnderstanding.route) {
                        HowDoILearnChartUnderstandingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnChart.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnDecision.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnDecision.route) {
                        HowDoILearnDecisionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnChartUnderstanding.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyHands.route) {
                        MyHandsIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MyHandsQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyHandsQuestion.route) {
                        MyHandsQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyHands.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyHandsCounting.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyHandsCounting.route) {
                        MyHandsCountingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyHandsQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyHandsFingers.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyHandsFingers.route) {
                        MyHandsFingersScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyHandsCounting.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyHandsConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyHandsConclusion.route) {
                        MyHandsConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyHandsFingers.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBody.route) {
                        MyBodyIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MyBodyFeetQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBodyFeetQuestion.route) {
                        MyBodyFeetQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyBody.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyBodyFeet.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBodyFeet.route) {
                        MyBodyFeetScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyBodyFeetQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyBodyEverythingQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBodyEverythingQuestion.route) {
                        MyBodyEverythingQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyBodyFeet.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyBodyImprovement.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBodyImprovement.route) {
                        MyBodyImprovementScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyBodyEverythingQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyBodyConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBodyConclusion.route) {
                        MyBodyConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyBodyImprovement.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHands.route) {
                        FirstHandsIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsQuestion.route) {
                        FirstHandsQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHands.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsKnowledge.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsKnowledge.route) {
                        FirstHandsKnowledgeScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHandsQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsHabilis.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsHabilis.route) {
                        FirstHandsHabilisScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHandsKnowledge.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsReflection.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsReflection.route) {
                        FirstHandsReflectionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHandsHabilis.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsCounting.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsCounting.route) {
                        FirstHandsCountingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHandsReflection.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsConclusion.route) {
                        FirstHandsConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHandsCounting.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstVoice.route) {
                        FirstVoiceIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.FirstVoiceVoice.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstVoiceVoice.route) {
                        FirstVoiceVoiceScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstVoice.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstVoiceReflection.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstVoiceReflection.route) {
                        FirstVoiceReflectionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstVoiceVoice.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstVoiceDifference.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstVoiceDifference.route) {
                        FirstVoiceDifferenceScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstVoiceReflection.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstVoiceConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstVoiceConclusion.route) {
                        FirstVoiceConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstVoiceDifference.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingIntro.route) {
                        DrawingAndCoutingIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingCircles.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingCircles.route) {
                        DrawingAndCoutingCirclesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingIntro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingRectangles.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingRectangles.route) {
                        DrawingAndCoutingRectanglesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingCircles.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingMesoamericans.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingMesoamericans.route) {
                        DrawingAndCoutingMesoamericansScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingRectangles.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingQuestion.route) {
                        DrawingAndCoutingQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingMesoamericans.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingExamples.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingExamples.route) {
                        DrawingAndCoutingExamplesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingConclusion.route) {
                        DrawingAndCoutingConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingExamples.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersIntro.route) {
                        NumbersIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.NumbersOrigin.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersOrigin.route) {
                        NumbersOriginScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NumbersIntro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NumbersQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersQuestion.route) {
                        NumbersQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NumbersOrigin.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NumbersEqual.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersEqual.route) {
                        NumbersEqualScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NumbersQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NumbersVisualizing.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersVisualizing.route) {
                        NumbersVisualizingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NumbersEqual.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NumbersConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersConclusion.route) {
                        NumbersConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NumbersVisualizing.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Intro.route) {
                        FamilyPart1IntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Orders.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Orders.route) {
                        FamilyPart1OrdersScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Intro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Order.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Order.route) {
                        FamilyPart1OrderScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Orders.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Altar.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Altar.route) {
                        FamilyPart1AltarScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Order.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Continuity.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Continuity.route) {
                        FamilyPart1ContinuityScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Altar.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Stages.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Stages.route) {
                        FamilyPart1StagesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Continuity.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Rule.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Rule.route) {
                        FamilyPart1RuleScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Stages.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Conclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Conclusion.route) {
                        FamilyPart1ConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Rule.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NaturalFamiliesPart2Intro.route) {
                        NaturalFamiliesPart2IntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.NaturalFamiliesPart2Question.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NaturalFamiliesPart2Question.route) {
                        NaturalFamiliesPart2QuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NaturalFamiliesPart2Intro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NaturalFamiliesPart2Logic.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NaturalFamiliesPart2Logic.route) {
                        NaturalFamiliesPart2LogicScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NaturalFamiliesPart2Question.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NaturalFamiliesPart2OrdersQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NaturalFamiliesPart2OrdersQuestion.route) {
                        NaturalFamiliesPart2OrdersQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NaturalFamiliesPart2Logic.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NaturalFamiliesPart2Copan.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NaturalFamiliesPart2Copan.route) {
                        NaturalFamiliesPart2CopanScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NaturalFamiliesPart2OrdersQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NaturalFamiliesPart2Naming.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NaturalFamiliesPart2Naming.route) {
                        NaturalFamiliesPart2NamingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NaturalFamiliesPart2Copan.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NaturalFamiliesPart2BillionQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NaturalFamiliesPart2BillionQuestion.route) {
                        NaturalFamiliesPart2BillionQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NaturalFamiliesPart2Naming.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NaturalFamiliesPart2Conclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NaturalFamiliesPart2Conclusion.route) {
                        NaturalFamiliesPart2ConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NaturalFamiliesPart2BillionQuestion.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TheZeroIntro.route) {
                        TheZeroIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.TheZeroWhatIs.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TheZeroWhatIs.route) {
                        TheZeroWhatIsScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.TheZeroIntro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.TheZeroIntuitive.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TheZeroIntuitive.route) {
                        TheZeroIntuitiveScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.TheZeroWhatIs.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.TheZeroNumber.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TheZeroNumber.route) {
                        TheZeroNumberScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.TheZeroIntuitive.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.TheZeroQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TheZeroQuestion.route) {
                        TheZeroQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.TheZeroNumber.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.TheZeroConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TheZeroConclusion.route) {
                        TheZeroConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.TheZeroQuestion.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TowardInfinityMaximum.route) {
                        TowardInfinityMaximumScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.TowardInfinityLaw.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TowardInfinityLaw.route) {
                        TowardInfinityLawScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.TowardInfinityMaximum.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.TowardInfinityWithoutLimits.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TowardInfinityWithoutLimits.route) {
                        TowardInfinityWithoutLimitsScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.TowardInfinityLaw.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.TowardInfinitySymbol.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TowardInfinitySymbol.route) {
                        TowardInfinitySymbolScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.TowardInfinityWithoutLimits.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.TowardInfinityDirection.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TowardInfinityDirection.route) {
                        TowardInfinityDirectionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.TowardInfinitySymbol.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.TowardInfinityConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.TowardInfinityConclusion.route) {
                        TowardInfinityConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.TowardInfinityDirection.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LimitsMinMaxBetweenBoth.route) {
                        LimitsMinMaxBetweenBothScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.LimitsMinMaxQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LimitsMinMaxQuestion.route) {
                        LimitsMinMaxQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LimitsMinMaxBetweenBoth.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LimitsMinMaxTending.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LimitsMinMaxTending.route) {
                        LimitsMinMaxTendingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LimitsMinMaxQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LimitsMinMaxHands.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LimitsMinMaxHands.route) {
                        LimitsMinMaxHandsScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LimitsMinMaxTending.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LimitsMinMaxConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LimitsMinMaxConclusion.route) {
                        LimitsMinMaxConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LimitsMinMaxHands.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.WhereAreTheyIntro.route) {
                        WhereAreTheyIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.WhereAreTheyInUs.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.WhereAreTheyInUs.route) {
                        WhereAreTheyInUsScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.WhereAreTheyIntro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.WhereAreTheyInTexts.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.WhereAreTheyInTexts.route) {
                        WhereAreTheyInTextsScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.WhereAreTheyInUs.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.WhereAreTheyAges.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.WhereAreTheyAges.route) {
                        WhereAreTheyAgesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.WhereAreTheyInTexts.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.WhereAreTheyQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.WhereAreTheyQuestion.route) {
                        WhereAreTheyQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.WhereAreTheyAges.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.WhereAreTheyWrongButRight.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.WhereAreTheyWrongButRight.route) {
                        WhereAreTheyWrongButRightScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.WhereAreTheyQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.WhereAreTheySpecies.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.WhereAreTheySpecies.route) {
                        WhereAreTheySpeciesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.WhereAreTheyWrongButRight.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.WhereAreTheyUniverse.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.WhereAreTheyUniverse.route) {
                        WhereAreTheyUniverseScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.WhereAreTheySpecies.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.WhereAreTheyConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.WhereAreTheyConclusion.route) {
                        WhereAreTheyConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.WhereAreTheyUniverse.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.Workout.route) {
                        WorkoutScreen(
                            scrollState = workoutScrollState,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToClap = { navController.navigate(Screen.Clap.route) },
                            onNavigateToFeetAndHands = { navController.navigate(Screen.FeetAndHands.route) },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) },
                            onNavigateToExercisingAddition = { navController.navigate(Screen.ExercisingAddition.route) },
                            onNavigateToRelationship = { navController.navigate(Screen.Relationship.route) },
                            onNavigateToExercisingMultiplicationL2 = { navController.navigate(Screen.ExercisingMultiplicationL2.route) }
                        )
                    }
                    composable(Screen.Abacus.route) {
                        AbacusScreen(
                            scrollState = abacusScrollState,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) },
                            onNavigateToSorobanWriting = { navController.navigate(Screen.SorobanWriting.route) },
                            onNavigateToSuanpanWriting = { navController.navigate(Screen.SuanpanWriting.route) },
                            onNavigateToSchyotyWriting = { navController.navigate(Screen.SchyotyWriting.route) },
                            onNavigateToLargeNumbersWriting = { navController.navigate(Screen.LargeNumbersWriting.route) },
                            onNavigateToPracticingAddition = { navController.navigate(Screen.PracticingAddition.route) },
                            onNavigateToMultiplicationTable = { navController.navigate(Screen.MultiplicationTable.route) },
                            onNavigateToMultiplyingWithAbacus = { navController.navigate(Screen.MultiplyingWithAbacus.route) },
                            onNavigateToMultiplyingWithAbacusLevel2 = { navController.navigate(Screen.MultiplyingWithAbacusLevel2.route) },
                            onNavigateToMultiplyingWithoutLimits = { navController.navigate(Screen.MultiplyingWithoutLimits.route) },
                            onNavigateToCarrying = { navController.navigate(Screen.Carrying.route) },
                            onNavigateToSubtractingWithAbacus = { navController.navigate(Screen.SubtractingWithAbacus.route) },
                            onNavigateToAddingWithAbacus = { navController.navigate(Screen.AddingWithAbacus.route) },
                            onNavigateToComplementToTen = { navController.navigate(Screen.ComplementToTen.route) },
                            onNavigateToAddingLargeNumbers = { navController.navigate(Screen.AddingLargeNumbers.route) },
                            onNavigateToHistory = { navController.navigate(Screen.AbacusHistoryIntro.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.Yupana.route) {
                        YupanaScreen(
                            scrollState = yupanaScrollState,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) },
                            onNavigateToPracticingAdditionYupana = { navController.navigate(Screen.PracticingAdditionYupana.route) },
                            onNavigateToPracticingMultiplicationYupana = { navController.navigate(Screen.PracticingMultiplicationYupana.route) },
                            onNavigateToHandsOnYupana = { navController.navigate(Screen.HandsOnYupana.route) { launchSingleTop = true } },
                            onNavigateToMovingInYupana = { navController.navigate(Screen.IskayMovement.route) { launchSingleTop = true } },
                            onNavigateToLargeNumbers = { navController.navigate(Screen.LargeNumbersIntro.route) { launchSingleTop = true } },
                            onNavigateToQuipuOnTheYupana = { navController.navigate(Screen.QuipuOnTheYupana.route) { launchSingleTop = true } },
                            onNavigateToMultiplyingWithYupana = { navController.navigate(Screen.MultiplyingWithYupanaIntro.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.HandsOnYupana.route) {
                        HandsOnYupanaScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToDrawingToCount = { navController.navigate(Screen.DrawingToCount.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingToCount.route) {
                        DrawingToCountScreen(
                            skinColor = skinColor,
                            onNavigateToHandsOnYupana = { navController.navigate(Screen.HandsOnYupana.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToYupana = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.IskayMovement.route) {
                        IskayMovementScreen(
                            skinColor = skinColor,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateToKimsa = { navController.navigate(Screen.KimsaMovement.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.KimsaMovement.route) {
                        KimsaMovementScreen(
                            skinColor = skinColor,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateToIskay = { navController.navigate(Screen.IskayMovement.route) { launchSingleTop = true } },
                            onNavigateToPisqa = { navController.navigate(Screen.PisqaMovement.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.PisqaMovement.route) {
                        PisqaMovementScreen(
                            skinColor = skinColor,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateToKimsa = { navController.navigate(Screen.KimsaMovement.route) { launchSingleTop = true } },
                            onNavigateToPichana = { navController.navigate(Screen.PichanaMovement.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.PichanaMovement.route) {
                        PichanaMovementScreen(
                            skinColor = skinColor,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateToPisqa = { navController.navigate(Screen.PisqaMovement.route) { launchSingleTop = true } },
                            onNavigateToKinkin = { navController.navigate(Screen.KinkinMovement.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.KinkinMovement.route) {
                        KinkinMovementScreen(
                            skinColor = skinColor,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateToPichana = { navController.navigate(Screen.PichanaMovement.route) { launchSingleTop = true } },
                            onNavigateToYupana = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.PracticingAdditionYupana.route) {
                        PracticingAdditionYupanaScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.QuipuOnTheYupana.route) {
                        QuipuOnTheYupanaScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.MesoamericanSymbolsIntro.route) {
                        MesoamericanSymbolsIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanSymbolsSystem.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanSymbolsSystem.route) {
                        MesoamericanSymbolsSystemScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanSymbolsIntro.route, false)) {
                                    navController.navigate(Screen.MesoamericanSymbolsIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanSymbolsThree.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanSymbolsThree.route) {
                        MesoamericanSymbolsThreeScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanSymbolsSystem.route, false)) {
                                    navController.navigate(Screen.MesoamericanSymbolsSystem.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanSymbolsZero.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanSymbolsZero.route) {
                        MesoamericanSymbolsZeroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanSymbolsThree.route, false)) {
                                    navController.navigate(Screen.MesoamericanSymbolsThree.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanSymbolsWrite.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanSymbolsWrite.route) {
                        MesoamericanSymbolsWriteScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanSymbolsZero.route, false)) {
                                    navController.navigate(Screen.MesoamericanSymbolsZero.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanSymbolsVertical.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanSymbolsThinking.route) {
                        MesoamericanSymbolsThinkingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanSymbolsVertical.route, false)) {
                                    navController.navigate(Screen.MesoamericanSymbolsVertical.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanSymbolsConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanSymbolsVertical.route) {
                        MesoamericanSymbolsVerticalScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanSymbolsWrite.route, false)) {
                                    navController.navigate(Screen.MesoamericanSymbolsWrite.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MesoamericanSymbolsThinking.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MesoamericanSymbolsConclusion.route) {
                        MesoamericanSymbolsConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MesoamericanSymbolsThinking.route, false)) {
                                    navController.navigate(Screen.MesoamericanSymbolsThinking.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToAnotherWayToCount = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.OvercomingLimitsIntro.route) {
                        OvercomingLimitsIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.OvercomingLimitsPreviousNumbers.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.OvercomingLimitsPreviousNumbers.route) {
                        OvercomingLimitsPreviousNumbersScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.OvercomingLimitsIntro.route, false)) {
                                    navController.navigate(Screen.OvercomingLimitsIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.OvercomingLimitsNextFive.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.OvercomingLimitsNextFive.route) {
                        OvercomingLimitsNextFiveScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.OvercomingLimitsPreviousNumbers.route, false)) {
                                    navController.navigate(Screen.OvercomingLimitsPreviousNumbers.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.OvercomingLimitsLogic.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.OvercomingLimitsLogic.route) {
                        OvercomingLimitsLogicScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.OvercomingLimitsNextFive.route, false)) {
                                    navController.navigate(Screen.OvercomingLimitsNextFive.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.OvercomingLimitsLastFoot.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.OvercomingLimitsLastFoot.route) {
                        OvercomingLimitsLastFootScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.OvercomingLimitsLogic.route, false)) {
                                    navController.navigate(Screen.OvercomingLimitsLogic.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.OvercomingLimitsConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.OvercomingLimitsConclusion.route) {
                        OvercomingLimitsConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.OvercomingLimitsLastFoot.route, false)) {
                                    navController.navigate(Screen.OvercomingLimitsLastFoot.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToAnotherWayToCount = {
                                if (!navController.popBackStack(Screen.AnotherWayToCount.route, false)) {
                                    navController.navigate(Screen.AnotherWayToCount.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(Screen.LargeNumbersIntro.route) {
                        LargeNumbersIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.LargeNumbersQuipu.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.LargeNumbersQuipu.route) {
                        LargeNumbersQuipuScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.LargeNumbersIntro.route, false)) {
                                    navController.navigate(Screen.LargeNumbersIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.LargeNumbersGrowing.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.LargeNumbersGrowing.route) {
                        LargeNumbersGrowingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.LargeNumbersQuipu.route, false)) {
                                    navController.navigate(Screen.LargeNumbersQuipu.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.LargeNumbersPattern.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.LargeNumbersPattern.route) {
                        LargeNumbersPatternScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.LargeNumbersGrowing.route, false)) {
                                    navController.navigate(Screen.LargeNumbersGrowing.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.LargeNumbersApp.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.LargeNumbersApp.route) {
                        LargeNumbersAppScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.LargeNumbersPattern.route, false)) {
                                    navController.navigate(Screen.LargeNumbersPattern.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.LargeNumbersConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.LargeNumbersConclusion.route) {
                        LargeNumbersConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.LargeNumbersApp.route, false)) {
                                    navController.navigate(Screen.LargeNumbersApp.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToYupana = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.MultiplyingWithYupanaIntro.route) {
                        MultiplyingWithYupanaIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MultiplyingWithYupanaFirstValues.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MultiplyingWithYupanaFirstValues.route) {
                        MultiplyingWithYupanaFirstValuesScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MultiplyingWithYupanaIntro.route, false)) {
                                    navController.navigate(Screen.MultiplyingWithYupanaIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MultiplyingWithYupanaNextStep.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MultiplyingWithYupanaNextStep.route) {
                        MultiplyingWithYupanaNextStepScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MultiplyingWithYupanaFirstValues.route, false)) {
                                    navController.navigate(Screen.MultiplyingWithYupanaFirstValues.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MultiplyingWithYupanaOrganizing.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MultiplyingWithYupanaOrganizing.route) {
                        MultiplyingWithYupanaOrganizingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MultiplyingWithYupanaNextStep.route, false)) {
                                    navController.navigate(Screen.MultiplyingWithYupanaNextStep.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MultiplyingWithYupanaThinking.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MultiplyingWithYupanaThinking.route) {
                        MultiplyingWithYupanaThinkingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MultiplyingWithYupanaOrganizing.route, false)) {
                                    navController.navigate(Screen.MultiplyingWithYupanaOrganizing.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MultiplyingWithYupanaProcess.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MultiplyingWithYupanaProcess.route) {
                        MultiplyingWithYupanaProcessScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MultiplyingWithYupanaThinking.route, false)) {
                                    navController.navigate(Screen.MultiplyingWithYupanaThinking.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MultiplyingWithYupanaConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.MultiplyingWithYupanaConclusion.route) {
                        MultiplyingWithYupanaConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.MultiplyingWithYupanaProcess.route, false)) {
                                    navController.navigate(Screen.MultiplyingWithYupanaProcess.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToYupana = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.PracticingMultiplicationYupana.route) {
                        PracticingMultiplicationYupanaScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SorobanWriting.route) {
                        SorobanWritingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SuanpanWriting.route) {
                        SuanpanWritingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SchyotyWriting.route) {
                        SchyotyWritingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LargeNumbersWriting.route) {
                        LargeNumbersWritingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.AbacusHistoryIntro.route) {
                        AbacusHistoryIntroScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AbacusHistorySuanpan.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AbacusHistorySuanpan.route) {
                        AbacusHistorySuanpanScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AbacusHistoryIntro.route, false)) {
                                    navController.navigate(Screen.AbacusHistoryIntro.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AbacusHistorySoroban.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AbacusHistorySoroban.route) {
                        AbacusHistorySorobanScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AbacusHistorySuanpan.route, false)) {
                                    navController.navigate(Screen.AbacusHistorySuanpan.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AbacusHistoryThinking.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AbacusHistoryThinking.route) {
                        AbacusHistoryThinkingScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AbacusHistorySoroban.route, false)) {
                                    navController.navigate(Screen.AbacusHistorySoroban.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AbacusHistorySimilarities.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AbacusHistorySimilarities.route) {
                        AbacusHistorySimilaritiesScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AbacusHistoryThinking.route, false)) {
                                    navController.navigate(Screen.AbacusHistoryThinking.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AbacusHistorySchyoty.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AbacusHistorySchyoty.route) {
                        AbacusHistorySchyotyScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AbacusHistorySimilarities.route, false)) {
                                    navController.navigate(Screen.AbacusHistorySimilarities.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AbacusHistoryCalculi.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AbacusHistoryCalculi.route) {
                        AbacusHistoryCalculiScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AbacusHistorySchyoty.route, false)) {
                                    navController.navigate(Screen.AbacusHistorySchyoty.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.AbacusHistoryConclusion.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.AbacusHistoryConclusion.route) {
                        AbacusHistoryConclusionScreen(
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) { launchSingleTop = true }
                                }
                            },
                            onNavigatePrev = {
                                if (!navController.popBackStack(Screen.AbacusHistoryCalculi.route, false)) {
                                    navController.navigate(Screen.AbacusHistoryCalculi.route) { launchSingleTop = true }
                                }
                            },
                            onNavigateToAbacus = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                    composable(
                        route = Screen.Content.route,
                        arguments = listOf(navArgument("fileName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val fileName = backStackEntry.arguments?.getString("fileName") ?: return@composable
                        ContentScreen(
                            fileName = fileName,
                            language = language,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateHome = {
                                navController.navigate(Screen.Index.route) {
                                    popUpTo(Screen.Index.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.IsItFree.route) {
                        IsItFreeScreen(
                            onNavigateToAbout = { navController.navigate(Screen.About.route) }
                        )
                    }
                    composable(Screen.About.route) {
                        AboutScreen()
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            currentLanguage = language,
                            currentBreakTime = breakTime,
                            currentSkinColor = skinColor,
                            currentCalendar = calendar,
                            onLanguageChanged = { lang ->
                                scope.launch { preferences.setLanguage(lang) }
                            },
                            onBreakTimeChanged = { minutes ->
                                scope.launch { preferences.setBreakTime(minutes) }
                            },
                            onSkinColorChanged = { color ->
                                scope.launch { preferences.setSkinColor(color) }
                            },
                            onCalendarChanged = { cal ->
                                scope.launch { preferences.setCalendar(cal) }
                            },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.LatestAddition.route) {
                        LatestAdditionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToPlayingWithAxioms = { navController.navigate(Screen.PlayingWithAxiomsGame.route) { launchSingleTop = true } },
                            onNavigateToCarryingInAdditionIntro = { navController.navigate(Screen.CarryingInAdditionIntro.route) { launchSingleTop = true } },
                            onNavigateToLargeNumbersIntro = { navController.navigate(Screen.LargeNumbersIntro.route) { launchSingleTop = true } },
                            onNavigateToQuipuOnTheYupana = { navController.navigate(Screen.QuipuOnTheYupana.route) { launchSingleTop = true } },
                            onNavigateToMesoamericanSymbols = { navController.navigate(Screen.MesoamericanSymbolsIntro.route) { launchSingleTop = true } },
                            onNavigateToOvercomingLimits = { navController.navigate(Screen.OvercomingLimitsIntro.route) { launchSingleTop = true } },
                            onNavigateToBuildingLikeAMesoamerican = { navController.navigate(Screen.BuildingLikeAMesoamerican.route) { launchSingleTop = true } },
                            onNavigateToMesoamericanOrders = { navController.navigate(Screen.MesoamericanOrdersIntro.route) { launchSingleTop = true } },
                            onNavigateToTextOrNumber = { navController.navigate(Screen.TextOrNumberIntro.route) { launchSingleTop = true } },
                            onNavigateToTheMissingNumbers = { navController.navigate(Screen.MissingNumbersIntro.route) { launchSingleTop = true } },
                            onNavigateToIPreferThis = { navController.navigate(Screen.IPreferThisIntro.route) { launchSingleTop = true } },
                            onNavigateToBuildingLikeEtruscanRomans = { navController.navigate(Screen.BuildingLikeEtruscanRomans.route) { launchSingleTop = true } },
                            onNavigateToEtruscanRomanTens = { navController.navigate(Screen.EtruscanRomanTensIntro.route) { launchSingleTop = true } },
                            onNavigateToHundredsAndThousands = { navController.navigate(Screen.HundredsAndThousandsIntro.route) { launchSingleTop = true } },
                            onNavigateToRepresentYou = { navController.navigate(Screen.IRepresentYou.route) { launchSingleTop = true } },
                            onNavigateToAbacusHistory = { navController.navigate(Screen.AbacusHistoryIntro.route) { launchSingleTop = true } },
                            onNavigateToMultiplyingWithYupana = { navController.navigate(Screen.MultiplyingWithYupanaIntro.route) { launchSingleTop = true } },
                            onNavigateToRunningAmongNumbersIntro = { navController.navigate(Screen.RunningAmongNumbersIntro.route) { launchSingleTop = true } },
                            onNavigateToSharedOriginIntro = { navController.navigate(Screen.SharedOriginIntro.route) { launchSingleTop = true } },
                            onNavigateToMatterAndEnergyIntro = { navController.navigate(Screen.MatterAndEnergyIntro.route) { launchSingleTop = true } },
                            onNavigateToCountingWithBonesIntro = { navController.navigate(Screen.CountingWithBonesIntro.route) { launchSingleTop = true } },
                            onNavigateToEverythingWasTogether = { navController.navigate(Screen.UniverseExpansion.route) { launchSingleTop = true } },
                            onNavigateToSharingWithWhomIntro = { navController.navigate(Screen.SharingWithWhomIntro.route) { launchSingleTop = true } },
                            onNavigateToQuipusIntro = { navController.navigate(Screen.QuipusIntro.route) { launchSingleTop = true } },
                            onNavigateToPracticingWithQuipus = { navController.navigate(Screen.PracticingWithQuipus.route) { launchSingleTop = true } },
                            onNavigateToEqualityIntro = { navController.navigate(Screen.EqualityIntro.route) { launchSingleTop = true } },
                            onNavigateToHistoricalEqualityIntro = { navController.navigate(Screen.HistoricalEqualityIntro.route) { launchSingleTop = true } },
                            onNavigateToHistoricalEqualityPyramidsIntro = { navController.navigate(Screen.HistoricalEqualityPyramidsIntro.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.Streak.route) {
                        StreakScreen(
                            streakCount = streakCount,
                            completedDates = completedDates,
                            streakDays = streakDays,
                            language = language,
                            calendarType = calendar,
                            reminderEnabled = reminderEnabled,
                            reminderHour = reminderHour,
                            reminderMinute = reminderMinute,
                            onStreakDaysChanged = { scope.launch { preferences.setStreakDays(it) } },
                            onReminderEnabledChanged = { scope.launch { preferences.setReminderEnabled(it) } },
                            onReminderTimeChanged = { hour, minute ->
                                scope.launch {
                                    preferences.setReminderHour(hour)
                                    preferences.setReminderMinute(minute)
                                }
                            },
                            onNavigateBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Clap.route) {
                        ClapScreen(
                            skinColor = skinColor,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Workout.route, false)) {
                                    navController.navigate(Screen.Workout.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FeetAndHands.route) {
                        FeetAndHandsScreen(
                            skinColor = skinColor,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Workout.route, false)) {
                                    navController.navigate(Screen.Workout.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.ExercisingAddition.route) {
                        ExercisingAdditionScreen(
                            skinColor = skinColor,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Workout.route, false)) {
                                    navController.navigate(Screen.Workout.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.Relationship.route) {
                        RelationshipScreen(
                            skinColor = skinColor,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Workout.route, false)) {
                                    navController.navigate(Screen.Workout.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.ExercisingMultiplicationL2.route) {
                        ExercisingMultiplicationL2Screen(
                            skinColor = skinColor,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Workout.route, false)) {
                                    navController.navigate(Screen.Workout.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.PracticingAddition.route) {
                        PracticingAdditionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MultiplicationTable.route) {
                        MultiplicationTableScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MultiplyingWithAbacus.route) {
                        MultiplyingWithAbacusScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MultiplyingWithAbacusLevel2.route) {
                        MultiplyingWithAbacusLevel2Screen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MultiplyingWithoutLimits.route) {
                        MultiplyingWithoutLimitsScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.Carrying.route) {
                        CarryingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SubtractingWithAbacus.route) {
                        SubtractingWithAbacusScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.AddingWithAbacus.route) {
                        AddingWithAbacusScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.ComplementToTen.route) {
                        ComplementToTenScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.AddingLargeNumbers.route) {
                        AddingLargeNumbersScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.Congratulation.route) {
                        CongratulationScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    if (showBreakDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(uiStrings.common.breakReminderTitle) },
            text = { Text(uiStrings.common.breakMessage) },
            confirmButton = {
                TextButton(onClick = {
                    breakStartTime = System.currentTimeMillis() / 1000L
                    showBreakDialog = false
                }) {
                    Text(uiStrings.common.imBack)
                }
            }
        )
    }
}
