# معماری و فلوی کار پروژه DastyarWordpress

## نمای کلی

این پروژه یک اپلیکیشن اندروید برای مدیریت وردپرس است که با استفاده از **Clean Architecture** و **Multi-Module Architecture** طراحی شده است.

---

## 🏗️ ساختار معماری

پروژه از **معماری سه لایه (Three-Layer Architecture)** استفاده می‌کند:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│    (UI, ViewModel, Navigation)          │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│          Domain Layer                   │
│    (UseCases, Repository Interface,     │
│         Domain Models)                  │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│           Data Layer                    │
│  (Repository Implementation,            │
│   DataSources, DTOs)                    │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│          Network Layer                  │
│    (Retrofit, API Services)             │
└─────────────────────────────────────────┘
```

---

## 📦 ساختار ماژول‌ها

### 1. **App Module** (`app/`)
- **مسئولیت**: نقطه ورودی اصلی اپلیکیشن
- **محتویات**:
  - `MainActivity.kt`: اکتیویتی اصلی که از Jetpack Compose استفاده می‌کند
  - `MyApp.kt`: کلاس Application با انوتیشن `@HiltAndroidApp` برای Hilt DI
  - `AppNavHost.kt`: مدیریت Navigation بین صفحات
  
**وابستگی‌ها**:
```kotlin
- feature:login
- feature:home
- androidx.navigation.compose
```

### 2. **Core Modules** (`core/`)

#### 2.1. **core:domain**
- **مسئولیت**: لایه منطق کسب‌وکار
- **محتویات**:
  - `model/Products.kt`: مدل‌های دامین (Domain Models)
  - `repository/ProductRepository.kt`: اینترفیس Repository
  - `usecase/GetProductUseCase.kt`: Use Case برای دریافت محصولات

**نکته مهم**: این ماژول هیچ وابستگی به Android یا فریمورک خاصی ندارد (Pure Kotlin).

```kotlin
class GetProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): List<Products> = repository.getProducts()
}
```

#### 2.2. **core:data**
- **مسئولیت**: پیاده‌سازی Repository و مدیریت منابع داده
- **محتویات**:
  - `repository/ProductRepositoryImpl.kt`: پیاده‌سازی Repository
  - `datasource/ProductRemoteDataSource.kt`: منبع داده از راه دور
  - `di/DataModule.kt`: ماژول Dependency Injection

**نقش کلیدی**: تبدیل DTO به Domain Model (Mapping)

```kotlin
fun ProductsDto.toDomain(): Products = Products(
    id = id,
    name = name,
    price = price,
    // ... mapping fields
)
```

#### 2.3. **core:network**
- **مسئولیت**: ارتباط با سرویس‌های وب (WooCommerce API)
- **محتویات**:
  - `api/ProductApi.kt`: تعریف API endpoints
  - `model/ProductsDto.kt`: Data Transfer Objects
  - `di/NetworkModule.kt`: پیکربندی Retrofit و OkHttp

**تکنولوژی‌ها**:
- Retrofit 2.9.0
- Kotlinx Serialization
- OkHttp Logging Interceptor

```kotlin
@Provides
@Singleton
fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory(contentType))
        .build()
}
```

#### 2.4. **core:common**
- مسئولیت: ابزارهای مشترک و Utilities

#### 2.5. **core:designsystem**
- مسئولیت: UI Components مشترک و تم‌بندی

#### 2.6. **core:model**
- مسئولیت: مدل‌های داده مشترک

#### 2.7. **core:ui**
- مسئولیت: UI Components قابل استفاده مجدد

### 3. **Feature Modules** (`feature/`)

#### 3.1. **feature:login**
- **محتویات**:
  - `splash/`: صفحه اسپلش
  - `introduction/`: صفحه معرفی
  - `login/`: صفحه ورود
  - `siteraddress/`: صفحه ورود آدرس سایت

**Navigation Flow**:
```
Splash → Introduction → Login → EnterShopAddress → Home
```

#### 3.2. **feature:home**
- **محتویات**:
  - `HomeScreen.kt`: صفحه نمایش لیست محصولات
  - `HomeViewModel.kt`: مدیریت state و business logic
  - `HomeNavigation.kt`: تعریف مسیر Navigation

**ViewModel Example**:
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProducts: GetProductUseCase
) : ViewModel() {
    var products by mutableStateOf<List<Products>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadProducts() {
        viewModelScope.launch {
            isLoading = true
            try {
                products = getProducts()
            } catch (e: Exception) {
                products = emptyList()
            } finally {
                isLoading = false
            }
        }
    }
}
```

### 4. **Build Logic** (`build-logic/`)
- **مسئولیت**: Convention Plugins برای مدیریت پیکربندی‌های Gradle

**Convention Plugins موجود**:
- `dastyarwordpress.android.application`
- `dastyarwordpress.android.library`
- `dastyarwordpress.android.feature`
- `dastyarwordpress.android.hilt`
- `dastyarwordpress.android.library.compose`
- `dastyarwordpress.network.dependencies`

---

## 🔄 فلوی کار (Data Flow)

### مثال: دریافت لیست محصولات

```
┌─────────────────┐
│   HomeScreen    │
│  (Composable)   │
└────────┬────────┘
         │ LaunchedEffect
         ↓
┌─────────────────┐
│  HomeViewModel  │
│ loadProducts()  │
└────────┬────────┘
         │ invoke
         ↓
┌──────────────────┐
│ GetProductUseCase│
└────────┬─────────┘
         │ getProducts()
         ↓
┌──────────────────────┐
│ ProductRepository    │
│    (Interface)       │
└────────┬─────────────┘
         │ implements
         ↓
┌──────────────────────┐
│ProductRepositoryImpl │
└────────┬─────────────┘
         │ getProducts()
         ↓
┌──────────────────────┐
│ProductRemoteDataSource│
└────────┬─────────────┘
         │ getProduct()
         ↓
┌──────────────────┐
│   ProductApi     │
│   (Retrofit)     │
└────────┬─────────┘
         │ HTTP GET
         ↓
┌──────────────────┐
│ WooCommerce API  │
│  /wp-json/wc/v3/ │
│    /products     │
└──────────────────┘
         │
         ↓ Response (List<ProductsDto>)
         │
┌──────────────────┐
│  Mapping to      │
│  Domain Model    │
│ (List<Products>) │
└────────┬─────────┘
         │
         ↓ Update State
┌─────────────────┐
│  HomeScreen     │
│   Re-compose    │
└─────────────────┘
```

---

## 🔧 Dependency Injection (Hilt)

### ساختار Module‌های Hilt:

#### 1. **NetworkModule** (`core:network`)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient
    
    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit
    
    @Provides @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi
}
```

#### 2. **DataModule** (`core:data`)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    fun provideProductRepository(
        remoteDataSource: ProductRemoteDataSource
    ): ProductRepository = ProductRepositoryImpl(remoteDataSource)
}
```

### گراف وابستگی:
```
MyApp (@HiltAndroidApp)
  ↓
MainActivity (@AndroidEntryPoint)
  ↓
HomeViewModel (@HiltViewModel)
  ↓
GetProductUseCase (@Inject)
  ↓
ProductRepository (provided by DataModule)
  ↓
ProductRemoteDataSource (@Inject)
  ↓
ProductApi (provided by NetworkModule)
```

---

## 🧭 Navigation Architecture

### Navigation Graph:
```kotlin
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    startDestination: KClass<*> = Splash::class
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        splashScreen(navigateToIntroduction = navController::navigateToIntroduction)
        introductionScreen(navigateToLogin = navController::navigateToLogin)
        enterShopAddressNavigation(navigateToEnterShopAddress = navController::navigateToEnterShopAddress)
        loginScreen(
            navigateToHome = navController::navigateToHome, 
            navigateToEnterShopAddressScreen = navController::navigateToEnterShopAddress
        )
        homeScreen()
    }
}
```

### Type-Safe Navigation:
پروژه از **Type-Safe Navigation** با استفاده از `KClass` استفاده می‌کند.

---

## 🛠️ تکنولوژی‌های استفاده شده

### Core Technologies:
- **Kotlin**: 1.9.23
- **Gradle**: 8.3.1
- **Compile SDK**: 34
- **Min SDK**: 23

### Libraries:

#### UI Layer:
- **Jetpack Compose**: 2024.05.00 BOM
- **Compose Compiler**: 1.5.12
- **Material 3**: Latest
- **Navigation Compose**: 2.8.0-beta01
- **Accompanist Pager**: 0.28.0

#### Dependency Injection:
- **Hilt**: 2.51.1
- **Hilt Navigation Compose**: 1.2.0

#### Networking:
- **Retrofit**: 2.9.0
- **OkHttp**: 4.12.0
- **Logging Interceptor**: 4.12.0
- **Kotlinx Serialization**: 1.6.3

#### Architecture Components:
- **ViewModel**: 2.8.0
- **Lifecycle Runtime Compose**: 2.8.0
- **Kotlin Coroutines**: 1.9.0

#### Build Configuration:
- **KSP**: 1.9.23-1.0.20
- **Android Gradle Plugin**: 8.3.1

---

## 📋 قواعد معماری

### 1. **Dependency Rule**
وابستگی‌ها فقط به سمت داخل (به سمت Domain) هستند:
```
Presentation → Domain ← Data ← Network
```

### 2. **Single Responsibility**
هر ماژول یک مسئولیت مشخص دارد:
- `domain`: Business Logic
- `data`: Data Management
- `network`: API Communication
- `feature`: UI Implementation

### 3. **Separation of Concerns**
- **UI**: فقط نمایش و تعامل با کاربر
- **ViewModel**: مدیریت State و Business Logic
- **UseCase**: یک عملیات کسب‌وکار
- **Repository**: مدیریت منابع داده

### 4. **Data Mapping**
- **DTO** (Data Transfer Object) در `network` layer
- **Domain Model** در `domain` layer
- تبدیل در `data` layer

---

## 🔐 امنیت

### Authentication:
پروژه از **Basic Authentication** برای WooCommerce API استفاده می‌کند:

```kotlin
val credentials = Credentials.basic(
    "ck_06637aa2c38c8e83a8de4cf401af1cd1fd2f2c1d",
    "cs_71cee146fa99a2c350103d7bd2fc9b18b619c9b9"
)
```

**⚠️ توجه**: کلیدهای API نباید در کد هاردکد شوند. باید از:
- BuildConfig
- Gradle Properties
- Environment Variables

استفاده شود.

---

## 🧪 Testing Strategy

### Test Layers:
```
Unit Tests (domain/)
  ↓
Integration Tests (data/)
  ↓
UI Tests (feature/)
```

### Tools Available:
- JUnit 4.13.2
- Espresso 3.5.1
- MockWebServer 4.12.0

---

## 🚀 Build Types

### Debug:
- Minify: Disabled
- Shrink Resources: Disabled
- Logging: Enabled

### Release:
- Minify: Enabled
- Shrink Resources: Enabled
- Logging: Disabled (باید تنظیم شود)

---

## 📱 صفحات و ویژگی‌های اپلیکیشن

### فلوی کاربر:

1. **Splash Screen** 
   - نمایش لوگو
   - چک اولیه
   
2. **Introduction Screen**
   - معرفی اپلیکیشن
   - ViewPager برای نمایش ویژگی‌ها
   
3. **Login Screen**
   - ورود کاربر
   - WebView برای احراز هویت
   
4. **Enter Shop Address**
   - وارد کردن آدرس فروشگاه WooCommerce
   
5. **Home Screen**
   - نمایش لیست محصولات
   - Loading State
   - Error Handling

---

## 🔄 State Management

### Pattern: **Unidirectional Data Flow (UDF)**

```
┌─────────────┐
│   Event     │ (User clicks button)
└──────┬──────┘
       │
       ↓
┌─────────────┐
│  ViewModel  │ (Process logic)
└──────┬──────┘
       │
       ↓
┌─────────────┐
│    State    │ (Update state)
└──────┬──────┘
       │
       ↓
┌─────────────┐
│     UI      │ (Re-compose)
└─────────────┘
```

### State Example:
```kotlin
// Mutable State in ViewModel
var products by mutableStateOf<List<Products>>(emptyList())
    private set

// Compose observes state automatically
LazyColumn {
    items(products) { product ->
        ProductItem(product)
    }
}
```

---

## 🎨 UI Architecture

### Compose Navigation:
هر Feature Module شامل:
1. **Screen Composable**: UI Component
2. **Navigation Function**: تعریف مسیر
3. **Navigate Function**: تابع ناوبری

```kotlin
// Navigation Definition
fun NavGraphBuilder.homeScreen() {
    composable<Home> {
        HomeRoute()
    }
}

// Navigate Function
fun NavController.navigateToHome() {
    navigate(Home)
}
```

---

## 📊 نمودار کامل معماری

```
┌────────────────────────────────────────────────────────────┐
│                        APP MODULE                          │
│  ┌──────────┐   ┌────────────┐   ┌───────────────┐       │
│  │ MyApp    │   │ MainActivity│   │ AppNavHost    │       │
│  │ @Hilt    │   │ @EntryPoint │   │ Navigation    │       │
│  └──────────┘   └────────────┘   └───────────────┘       │
└─────────────────┬──────────────────────────────┬──────────┘
                  │                              │
        ┌─────────┴─────────┐         ┌─────────┴─────────┐
        │                   │         │                   │
┌───────▼─────────┐ ┌───────▼─────────┐                 │
│ FEATURE: LOGIN  │ │ FEATURE: HOME   │                 │
│ ┌─────────────┐ │ │ ┌─────────────┐ │                 │
│ │   Screen    │ │ │ │   Screen    │ │                 │
│ │  Navigation │ │ │ │  ViewModel  │ │                 │
│ └─────────────┘ │ │ └──────┬──────┘ │                 │
└─────────────────┘ └─────────┼────────┘                 │
                              │                           │
                    ┌─────────▼──────────┐               │
                    │   CORE: DOMAIN     │               │
                    │ ┌────────────────┐ │               │
                    │ │   Use Cases    │ │               │
                    │ │   Repository   │ │               │
                    │ │   Interface    │ │               │
                    │ │   Domain Model │ │               │
                    │ └────────┬───────┘ │               │
                    └──────────┼─────────┘               │
                               │                          │
                    ┌──────────▼─────────┐               │
                    │    CORE: DATA      │               │
                    │ ┌────────────────┐ │               │
                    │ │  Repository    │ │               │
                    │ │  Implementation│ │               │
                    │ │  DataSource    │ │               │
                    │ │  Mapping       │ │               │
                    │ └────────┬───────┘ │               │
                    └──────────┼─────────┘               │
                               │                          │
                    ┌──────────▼─────────┐               │
                    │   CORE: NETWORK    │               │
                    │ ┌────────────────┐ │               │
                    │ │  Retrofit API  │ │               │
                    │ │  OkHttp Client │ │               │
                    │ │  DTO Models    │ │               │
                    │ │  Interceptors  │ │               │
                    │ └────────┬───────┘ │               │
                    └──────────┼─────────┘               │
                               │                          │
                    ┌──────────▼──────────┐              │
                    │  WooCommerce API    │              │
                    │  /wp-json/wc/v3/    │              │
                    └─────────────────────┘              │
                                                          │
┌─────────────────────────────────────────────────────────┘
│  CORE MODULES (Shared)
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│  │  common  │ │  model   │ │designsys │ │    ui    │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘
└──────────────────────────────────────────────────────────
```

---

## 🔍 نکات مهم

### 1. Convention Plugins
این پروژه از **Convention Plugins** استفاده می‌کند که مزایای آن:
- کاهش تکرار در `build.gradle.kts`
- مدیریت متمرکز پیکربندی‌ها
- سهولت در به‌روزرسانی

### 2. Type-Safe Project Accessor
با فعال کردن `TYPESAFE_PROJECT_ACCESSORS`:
```kotlin
// به جای:
implementation(project(":feature:login"))

// می‌توان نوشت:
implementation(projects.feature.login)
```

### 3. Kotlin DSL
تمام فایل‌های Gradle با Kotlin DSL (`.kts`) نوشته شده‌اند.

### 4. Version Catalog
مدیریت نسخه‌های کتابخانه‌ها در `gradle/libs.versions.toml`

---

## 📈 مسیر توسعه آینده

### پیشنهادات برای بهبود:

1. **Repository Layer**:
   - اضافه کردن Local Database (Room)
   - Caching Strategy
   - Offline-First Architecture

2. **Security**:
   - انتقال API Keys به BuildConfig
   - استفاده از Encrypted SharedPreferences

3. **Error Handling**:
   - پیاده‌سازی Result/Either Pattern
   - Custom Exception Classes
   - User-Friendly Error Messages

4. **Testing**:
   - Unit Tests برای UseCases
   - Repository Tests با MockWebServer
   - UI Tests با Compose Testing

5. **Performance**:
   - Pagination برای لیست محصولات
   - Image Loading با Coil
   - Memory Leak Prevention

6. **UI/UX**:
   - Pull-to-Refresh
   - Empty State
   - Error State
   - Shimmer Loading

---

## 📝 خلاصه

این پروژه یک نمونه عالی از **Clean Architecture** در اندروید است که:

✅ **Modular**: ماژول‌های جداگانه برای هر مسئولیت  
✅ **Scalable**: قابل گسترش برای ویژگی‌های جدید  
✅ **Testable**: قابلیت تست در تمام لایه‌ها  
✅ **Maintainable**: قابل نگهداری و خوانا  
✅ **Modern**: استفاده از جدیدترین تکنولوژی‌های اندروید  

**تکنولوژی‌های کلیدی**:
- Jetpack Compose
- Hilt Dependency Injection
- Kotlin Coroutines
- Retrofit + Kotlinx Serialization
- Navigation Compose
- Clean Architecture
- Multi-Module Structure

---

**نسخه مستند**: 1.0  
**تاریخ**: نوامبر 2025  
**نگهدارنده**: تیم DastyarWordpress

