package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Atelier", appName)
  }

  @Test
  fun `categories management adds and persists categories`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.FirebaseAtelierRepository(context)
    val initialCats = repo.categories.value
    org.junit.Assert.assertTrue(initialCats.contains("Haute Couture"))
    org.junit.Assert.assertTrue(initialCats.contains("Evening Wear"))
    org.junit.Assert.assertTrue(initialCats.contains("Tailoring"))

    // Add a new custom category
    val added = repo.addCategory("Resortwear 2027")
    org.junit.Assert.assertTrue(added)
    org.junit.Assert.assertTrue(repo.categories.value.contains("Resortwear 2027"))

    // Adding duplicate category returns false
    val duplicate = repo.addCategory("resortwear 2027")
    org.junit.Assert.assertFalse(duplicate)

    // Adding blank category returns false
    val blank = repo.addCategory("   ")
    org.junit.Assert.assertFalse(blank)

    // Verify persistence across new repo instance
    val repo2 = com.example.data.FirebaseAtelierRepository(context)
    org.junit.Assert.assertTrue(repo2.categories.value.contains("Resortwear 2027"))
  }
}
