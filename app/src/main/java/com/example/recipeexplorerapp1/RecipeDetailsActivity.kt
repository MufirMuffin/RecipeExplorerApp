package com.example.recipeexplorerapp1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.recipeexplorerapp1.base.BaseActivity
import com.example.recipeexplorerapp1.entity.RecipeListingData
import com.example.recipeexplorerapp1.utils.bind
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import javax.inject.Inject

class RecipeDetailsActivity : BaseActivity(), IRecipeDetailsView {

    @Inject
    lateinit var presenter: RecipeDetailsPresenter

    private lateinit var appBar: AppBarLayout
    private lateinit var toolbarTitle: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: TextView
    private lateinit var cuisineText: TextView
    private lateinit var difficultyText: TextView
    private lateinit var prepTimeText: TextView
    private lateinit var cookTimeText: TextView
    private lateinit var servingsText: TextView
    private lateinit var caloriesText: TextView
    private lateinit var ingredientsContainer: LinearLayout
    private lateinit var instructionsContainer: LinearLayout
    private lateinit var tagsChipGroup: ChipGroup

    private var recipeId: Int = 0

    companion object {
        private const val EXTRA_RECIPE_ID = "extra_recipe_id"

        fun show(context: Context, recipeId: Int) {
            val intent = Intent(context, RecipeDetailsActivity::class.java)
            intent.putExtra(EXTRA_RECIPE_ID, recipeId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, 0)

        DaggerRecipeDetailsComponent.builder()
            .applicationComponent(Application.component)
            .recipeDetailsModule(RecipeDetailsModule(this))
            .build()
            .inject(this)

        initViews()
        setupToolbar()

        presenter.loadRecipeDetails(recipeId, false)
    }

    private fun initViews() {
        appBar = bind(R.id.app_bar)
        toolbarTitle = bind(R.id.toolbar_title)
        recipeImage = bind(R.id.recipe_detail_image)
        recipeName = bind(R.id.recipe_name)
        cuisineText = bind(R.id.cuisine_text)
        difficultyText = bind(R.id.difficulty_text)
        prepTimeText = bind(R.id.prep_time_text)
        cookTimeText = bind(R.id.cook_time_text)
        servingsText = bind(R.id.servings_text)
        caloriesText = bind(R.id.calories_text)
        ingredientsContainer = bind(R.id.ingredients_container)
        instructionsContainer = bind(R.id.instructions_container)
        tagsChipGroup = bind(R.id.tags_chip_group)
    }

    private fun setupToolbar() {
        setSupportActionBar(bind(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = null
        toolbarTitle.text = "Recipe Details"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun attachPresenter(recreated: Boolean) {
        presenter.attachView(this, recreated)
    }

    override fun deattachPresenter() {
        presenter.deattachView()
    }

    override fun showRecipeDetails(recipe: RecipeListingData) {
        // Load image
        GlideApp.with(this)
            .load(recipe.imageUrl)
            .placeholder(R.drawable.default_profile_img)
            .into(recipeImage)

        // Set basic info
        recipeName.text = recipe.title
        cuisineText.text = recipe.cuisine
        difficultyText.text = recipe.difficulty
        prepTimeText.text = "${recipe.prepTimeMinutes} min"
        cookTimeText.text = "${recipe.cookTimeMinutes} min"
        servingsText.text = "${recipe.servings} servings"
        caloriesText.text = "${recipe.caloriesPerServing} cal"

        // Display ingredients
        ingredientsContainer.removeAllViews()
        recipe.ingredients?.forEachIndexed { index, ingredient ->
            val ingredientView = layoutInflater.inflate(
                R.layout.item_ingredient,
                ingredientsContainer,
                false
            ) as TextView
            ingredientView.text = "• $ingredient"
            ingredientsContainer.addView(ingredientView)
        }

        // Display instructions
        instructionsContainer.removeAllViews()
        recipe.instructions?.forEachIndexed { index, instruction ->
            val instructionView = layoutInflater.inflate(
                R.layout.item_instruction,
                instructionsContainer,
                false
            ) as TextView
            instructionView.text = "${index + 1}. $instruction"
            instructionsContainer.addView(instructionView)
        }

        // Display tags
        tagsChipGroup.removeAllViews()
        recipe.tags?.forEach { tag ->
            val chip = Chip(this)
            chip.text = tag
            chip.isClickable = false
            chip.isCheckable = false
            chip.setChipBackgroundColorResource(R.color.chip_background)
            chip.setTextColor(ContextCompat.getColor(this, R.color.chip_text))
            tagsChipGroup.addView(chip)
        }

        showContent()
    }
}