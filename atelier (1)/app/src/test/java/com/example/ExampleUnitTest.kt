package com.example

import com.example.data.BaselineGarments
import com.example.ui.AtelierTab
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun atelierTabs_areNotNullAndHaveCorrectRoutes() {
    val tabs = AtelierTab.items
    assertEquals(4, tabs.size)
    for (tab in tabs) {
      assertNotNull("Tab must not be null", tab)
      assertNotNull("Tab route must not be null", tab.route)
      assertNotNull("Tab title must not be null", tab.title)
    }
    assertEquals("home", tabs[0].route)
    assertEquals("generate", tabs[1].route)
    assertEquals("library", tabs[2].route)
    assertEquals("more", tabs[3].route)
  }

  @Test
  fun baselineGarments_contains505DistinctGarments() {
    val baseline = BaselineGarments.getInitialBaseline()
    assertEquals(505, baseline.size)
    val uniqueTitles = baseline.map { it.title }.toSet()
    assertEquals(505, uniqueTitles.size)
    for (item in baseline) {
      assertTrue("Item ID should not be blank", item.id.isNotBlank())
      assertTrue("Title should not be blank", item.title.isNotBlank())
      assertTrue("Category should not be blank", item.category.isNotBlank())
      assertTrue("Fabric should not be blank", item.fabric.isNotBlank())
      assertTrue("Silhouette should not be blank", item.silhouette.isNotBlank())
      assertFalse("Baseline garment should not be marked custom", item.isCustom)
      assertNull("Baseline garment should have null createdBy", item.createdBy)
    }
  }
}
