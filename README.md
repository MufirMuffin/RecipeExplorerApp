Recipe Explorer App

📱 App Features

✨ Core Features

Recipe List: Browse all recipes with beautiful cards displaying title and image
Recipe Details: Detailed view for each recipe with comprehensive information
Smart Search: Real-time search functionality using the API's q parameter
Difficulty Filtering: Client-side filtering by difficulty level (Easy, Medium, Hard)
Pull-to-Refresh: Smooth refresh gesture to update recipe list

🚀 Advanced Features

Infinite Scroll: Automatic pagination with seamless loading of more recipes
Pagination: Efficient data loading with 10 recipes per page
Error Handling: Graceful error handling with user-friendly messages
Modern UI: Material Design 3 components with dark/light theme support

🛠 Technical Implementation

🏗️ Architecture

MVVM Pattern: Clean separation of concerns with ViewModels
Dependency Injection: Manual DI with Dagger-like components
Reactive UI: LiveData and observables for data binding
Material Design 3: Modern Android UI components

Key Components

MainActivity: Recipe listing with search and filters
RecipeDetailsActivity: Detailed recipe view
MainPresenter: Business logic and API coordination
RecipeAdapter: RecyclerView adapter for efficient list rendering
Pagination: 10 recipes per request, Load Trigger: When user scrolls to last 3 items
Filtering: Type: Client-side filtering, Instant results, no API calls

Screenshots:

<img width="1080" height="2424" alt="Screenshot_20251022_084614" src="https://github.com/user-attachments/assets/42d45b14-d916-49de-a247-da96cedb1d12" />
<img width="1080" height="2424" alt="image" src="https://github.com/user-attachments/assets/1922173d-c078-4d85-99e0-7457f56e5734" />
<img width="1080" height="2400" alt="Screenshot_20251020_003824" src="https://github.com/user-attachments/assets/fe310883-da90-450c-97b7-929467407171" />
<img width="1080" height="2424" alt="Screenshot_20251022_084638" src="https://github.com/user-attachments/assets/a733a27d-047d-43a8-9303-860229b079ea" />
<img width="1080" height="2424" alt="Screenshot_20251022_084654" src="https://github.com/user-attachments/assets/2b994886-663e-424d-87f1-87b5eca11a52" />
<img width="1080" height="2424" alt="Screenshot_20251022_084700" src="https://github.com/user-attachments/assets/d4687fc4-4516-44dd-883f-60af258d7159" />

👥 Authors

Muhammad Firdaus Fauzi - Initial work

🙏 Acknowledgments

DummyJSON - For providing the free recipe API
Material Design - For design guidelines
Android Developers - For excellent documentation
Open source community for various libraries used
ClaudeAI - For AI-assisted development guidance

📞 Contact

Email: ffdausfauzi8@gmail.com
LinkedIn: www.linkedin.com/in/mfirdausfauzi
