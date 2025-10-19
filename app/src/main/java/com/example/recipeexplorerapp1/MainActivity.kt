package com.example.recipeexplorerapp1

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.recipeexplorerapp1.base.BaseActivity
import com.example.recipeexplorerapp1.base.IRecipeCardPresenter
import com.example.recipeexplorerapp1.entity.RecipeListingData
import com.example.recipeexplorerapp1.utils.bind
import com.github.stephenvinouze.advancedrecyclerview.core.adapters.RecyclerAdapter
import com.github.stephenvinouze.advancedrecyclerview.core.enums.ChoiceMode
import com.google.android.material.appbar.AppBarLayout
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity(), IMainView, TextWatcher {

    @Inject
    lateinit var presenter: MainPresenter
    lateinit var appBar: AppBarLayout
    lateinit var toolbarTitle: TextView
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecipeAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loadMoreProgress: ProgressBar

    private var allRecipes = mutableListOf<RecipeListingData>()
    private var filteredRecipes = mutableListOf<RecipeListingData>()

    private lateinit var searchET: EditText
    private lateinit var difficultySpinner: Spinner
    private lateinit var emptySearchHolder: View

    private var isLoadingMore = false
    private var currentSearchQuery = ""
    private var currentDifficultyFilter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        DaggerMainComponent.builder().applicationComponent(Application.component)
            .mainModule(MainModule(this)).build().inject(this)

        setSupportActionBar(bind(R.id.toolbar))
        supportActionBar?.title = null
        supportActionBar?.show()
        appBar = bind(R.id.app_bar)
        toolbarTitle = bind(R.id.activity_main_toolbar_title)

        recipeRecyclerView = bind(R.id.recipe_recycler_view)
        emptySearchHolder = bind(R.id.empty_holder_listing)
        swipeRefreshLayout = bind(R.id.swipeContainer)
        searchET = bind(R.id.search_et)
        difficultySpinner = bind(R.id.difficulty_spinner)
        loadMoreProgress = bind(R.id.load_more_progress)

        toolbarTitle.text = "Recipe Explorer"

        setupDifficultySpinner()
        searchET.addTextChangedListener(this)

        recyclerViewAdapter = RecipeAdapter(this, presenter)
        recyclerViewAdapter.choiceMode = ChoiceMode.NONE
        recipeRecyclerView.adapter = recyclerViewAdapter

        val layoutManager = LinearLayoutManager(this)
        recipeRecyclerView.setHasFixedSize(true)
        recipeRecyclerView.layoutManager = layoutManager
        recipeRecyclerView.isNestedScrollingEnabled = true

        // Infinite Scroll Listener
        recipeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Only load more if not filtering (search or difficulty)
                if (currentSearchQuery.isEmpty() && currentDifficultyFilter == "All") {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Load more when reaching the last 3 items
                    if (!isLoadingMore &&
                        (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3 &&
                        firstVisibleItemPosition >= 0 &&
                        totalItemCount >= 10
                    ) {
                        Timber.d("Infinite scroll triggered - loading more recipes")
                        presenter.loadMoreRecipes()
                    }
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            allRecipes.clear()
            filteredRecipes.clear()
            presenter.resetPagination()
            presenter.loadAllRecipes(true)
        }

        presenter.loadAllRecipes(false)
    }

    private fun setupDifficultySpinner() {
        val difficulties = listOf("All", "Easy", "Medium", "Hard")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, difficulties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = adapter

        difficultySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentDifficultyFilter = difficulties[position]
                Timber.d("Difficulty filter changed to: $currentDifficultyFilter")
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    override fun attachPresenter(recreated: Boolean) {
        presenter.attachView(this, recreated)
    }

    override fun deattachPresenter() {
        presenter.deattachView()
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(p0: Editable?) {
        p0?.let {
            currentSearchQuery = it.toString().trim()
            applyFilters()
        }
    }

    private fun applyFilters() {
        if (currentSearchQuery.isEmpty() && currentDifficultyFilter == "All") {
            // No filters applied, show all recipes
            showAllRecipes()
        } else {
            // Apply filters
            filterRecipes()
        }
    }

    private fun showAllRecipes() {
        recyclerViewAdapter.items = allRecipes
        recyclerViewAdapter.notifyDataSetChanged()

        if (allRecipes.isEmpty()) {
            emptySearchHolder.visibility = View.VISIBLE
        } else {
            emptySearchHolder.visibility = View.GONE
        }
    }

    private fun filterRecipes() {
        filteredRecipes = allRecipes.filter { recipe ->
            // Search filter
            val matchesSearch = if (currentSearchQuery.isEmpty()) {
                true
            } else {
                recipe.title.contains(currentSearchQuery, true) ||
                        recipe.cuisine.contains(currentSearchQuery, true) ||
                        recipe.tags?.any { it.contains(currentSearchQuery, true) } == true
            }

            // Difficulty filter
            val matchesDifficulty = if (currentDifficultyFilter == "All") {
                true
            } else {
                recipe.difficulty.equals(currentDifficultyFilter, ignoreCase = true)
            }

            matchesSearch && matchesDifficulty
        }.toMutableList()

        Timber.d("Filtered: ${filteredRecipes.size} recipes (search: '$currentSearchQuery', difficulty: '$currentDifficultyFilter')")

        recyclerViewAdapter.items = filteredRecipes
        recyclerViewAdapter.notifyDataSetChanged()

        if (filteredRecipes.isEmpty()) {
            emptySearchHolder.visibility = View.VISIBLE
        } else {
            emptySearchHolder.visibility = View.GONE
        }
    }

    override fun showEmptyHolder() {
        emptySearchHolder.visibility = View.VISIBLE
    }

    override fun hideEmptyHolder() {
        emptySearchHolder.visibility = View.GONE
    }

    override fun showLoadMoreProgress() {
        isLoadingMore = true
        loadMoreProgress.visibility = View.VISIBLE
        Timber.d("Showing load more progress")
    }

    override fun hideLoadMoreProgress() {
        isLoadingMore = false
        loadMoreProgress.visibility = View.GONE
        Timber.d("Hiding load more progress")
    }

    override fun appendRecipes(recipes: List<RecipeListingData>) {
        if (recipes.isEmpty()) {
            Timber.d("No recipes to append")
            return
        }

        val startPosition = allRecipes.size
        allRecipes.addAll(recipes)

        Timber.d("Appended ${recipes.size} recipes, total now: ${allRecipes.size}")

        // If no filters are active, update the display
        if (currentSearchQuery.isEmpty() && currentDifficultyFilter == "All") {
            recyclerViewAdapter.items = allRecipes
            recyclerViewAdapter.notifyItemRangeInserted(startPosition, recipes.size)
        } else {
            // If filters are active, just add to cache (don't display)
            Timber.d("Filters active, recipes added to cache but not displayed")
        }
    }

    override fun showRecipe(model: RecipeListingData) {
        RecipeDetailsActivity.show(this, model.id)
    }

    override fun showRecipes(recipes: List<RecipeListingData>) {
        allRecipes.clear()
        filteredRecipes.clear()

        if (recipes.isNotEmpty()) {
            allRecipes.addAll(recipes)
        }

        swipeRefreshLayout.isRefreshing = false

        if (allRecipes.isEmpty()) {
            emptySearchHolder.visibility = View.VISIBLE
            recipeRecyclerView.visibility = View.GONE
        } else {
            emptySearchHolder.visibility = View.GONE
            recipeRecyclerView.visibility = View.VISIBLE
        }

        // Apply current filters
        if (currentSearchQuery.isEmpty() && currentDifficultyFilter == "All") {
            recyclerViewAdapter.items = allRecipes
        } else {
            filterRecipes()
            return // filterRecipes handles notifyDataSetChanged
        }

        recyclerViewAdapter.notifyDataSetChanged()
        showContent()
    }
}

class RecipeItemView @JvmOverloads constructor(
    context: Context,
    var presenter: IRecipeCardPresenter,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var iconRecipe: ImageView
    private lateinit var titleRecipe: TextView

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        LayoutInflater.from(context).inflate(R.layout.recipe_card, this, true)
        initViews()
    }

    private fun initViews() {
        iconRecipe = findViewById(R.id.recipe_iv)
        titleRecipe = findViewById(R.id.recipe_tv)

        isClickable = true
        isFocusable = true
    }

    fun bind(model: RecipeListingData) {
        GlideApp.with(context)
            .load(model.imageUrl)
            .placeholder(R.drawable.default_profile_img)
            .dontTransform()
            .into(iconRecipe)
        titleRecipe.text = model.title

        setOnClickListener {
            presenter.clickOnRecipe(model)
            Timber.d("clicked on clickOnRecipe for: ${model.title}")
        }
    }
}

class RecipeAdapter(context: Context, val presenter: IRecipeCardPresenter) :
    RecyclerAdapter<RecipeListingData>(context) {

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): View =
        RecipeItemView(context, presenter = presenter)

    override fun onBindItemView(view: View, position: Int) {
        when (view) {
            is RecipeItemView -> {
                view.bind(items[position])
            }
        }
    }
}