---
title: Lifecycle & State
---

# Lifecycle & State in Compose

This guide covers how Koin integrates with Compose's lifecycle and state management. Understanding these concepts helps you write efficient, bug-free Compose applications.

:::info
This guide aligns with [Android's official Compose lifecycle documentation](https://developer.android.com/develop/ui/compose/lifecycle).
:::

## Compose Lifecycle Overview

A Composable has three lifecycle events:

1. **Enter Composition** - Composable is first called
2. **Recomposition** - Composable re-executes when state changes (0 or more times)
3. **Leave Composition** - Composable is removed from the tree

Koin's Compose APIs are designed to work efficiently with this lifecycle.

## Injection and Recomposition

### How koinInject() Works

`koinInject()` retrieves instances from Koin and **remembers** them across recompositions:

```kotlin
@Composable
fun MyScreen() {
    // Resolved once, remembered across recompositions
    val repository = koinInject<UserRepository>()

    // Safe - uses the same instance
    val users by repository.users.collectAsState()
}
```

### Injection Timing

Inject dependencies at the **Composable function level**, not inside callbacks:

```kotlin
@Composable
fun MyScreen() {
    // Correct - resolved at composition time
    val repository = koinInject<UserRepository>()
    val viewModel = koinViewModel<MyViewModel>()

    Button(onClick = {
        // Wrong - don't inject in callbacks
        val service = koinInject<Service>() // Avoid!

        // Correct - use already-injected instance
        repository.save()
    }) {
        Text("Save")
    }
}
```

### Performance with Parameters

When using parameters with `koinInject`, prefer the explicit parameter form:

```kotlin
@Composable
fun MyScreen(userId: String) {
    // More efficient - parameters evaluated once
    val presenter = koinInject<UserPresenter>(
        parameters = parametersOf(userId)
    )

    // Less efficient - lambda re-evaluated on recomposition
    val presenter = koinInject<UserPresenter> {
        parametersOf(userId)
    }
}
```

## State Management with Koin

### StateFlow and collectAsState

The standard pattern for reactive UI with Koin:

```kotlin
@KoinViewModel
class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.value = UiState.Success(repository.getUsers())
        }
    }
}

@Composable
fun UserScreen(
    viewModel: UserViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    when (val s = state) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Success -> UserList(s.users)
        is UiState.Error -> ErrorMessage(s.message)
    }
}
```

### Direct Repository Injection

For simpler cases, inject repositories directly:

```kotlin
@Singleton
class UserRepository {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()
}

@Composable
fun UserListScreen() {
    val repository = koinInject<UserRepository>()
    val users by repository.users.collectAsState()

    LazyColumn {
        items(users) { user ->
            UserCard(user)
        }
    }
}
```

### remember() vs koinInject()

Use the right tool for each job:

```kotlin
@Composable
fun MyScreen() {
    // Koin-managed dependencies
    val viewModel = koinViewModel<MyViewModel>()
    val repository = koinInject<Repository>()

    // Compose-managed state
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }

    // Don't wrap koinInject in remember (unnecessary)
    val service = remember { koinInject<Service>() } // Redundant!
}
```

## Side Effects with Koin

### LaunchedEffect

Execute suspending code when composition enters or keys change:

```kotlin
@Composable
fun UserDetailScreen(userId: String) {
    val repository = koinInject<UserRepository>()
    var user by remember { mutableStateOf<User?>(null) }

    // Runs when userId changes
    LaunchedEffect(userId) {
        user = repository.getUser(userId)
    }

    user?.let { UserContent(it) }
}
```

### DisposableEffect

Clean up resources when leaving composition:

```kotlin
@Composable
fun EventScreen() {
    val eventBus = koinInject<EventBus>()

    DisposableEffect(Unit) {
        val listener = eventBus.subscribe { event ->
            // Handle event
        }

        onDispose {
            eventBus.unsubscribe(listener)
        }
    }
}
```

### SideEffect

Execute non-suspending side effects after every successful recomposition:

```kotlin
@Composable
fun AnalyticsScreen(screenName: String) {
    val analytics = koinInject<Analytics>()

    SideEffect {
        analytics.logScreenView(screenName)
    }
}
```

## Stability and Skipping

### Understanding Stable Types

Compose can skip recomposition when inputs haven't changed. For this to work, parameter types must be **stable**:

```kotlin
// Stable - Compose can skip
@Composable
fun UserCard(
    name: String,                    // Primitive - stable
    onClick: () -> Unit,             // Lambda - stable
    viewModel: UserViewModel = koinViewModel()  // Treated as stable
)

// Potentially unstable - may not skip
@Composable
fun UserCard(
    user: User  // Data class - stable if all properties stable
)
```

### Koin Injections and Stability

Koin injections are treated as stable because they return the same instance (for singletons) or are remembered:

```kotlin
@Composable
fun MyScreen() {
    // Stable - singleton returns same instance
    val repository = koinInject<UserRepository>()

    // Stable - ViewModel is remembered
    val viewModel = koinViewModel<MyViewModel>()
}
```

## Passing Parameters vs Injection

### Decision Guide

| Pass as Parameter | Inject with Koin |
|-------------------|------------------|
| Changes frequently (userId, query) | Stable dependencies (repositories, services) |
| UI state (selected item) | Infrastructure (database, network) |
| Navigation arguments | Business logic (use cases) |
| Parent-provided data | ViewModels |

### Example Pattern

```kotlin
// userId changes - pass as parameter
// repository is stable - inject
@Composable
fun UserProfile(
    userId: String,
    repository: UserRepository = koinInject()
) {
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        user = repository.getUser(userId)
    }

    user?.let { ProfileContent(it) }
}

// Pure composable - no injection needed
@Composable
fun ProfileContent(user: User) {
    Column {
        Text(user.name)
        Text(user.email)
    }
}
```

## Best Practices

### 1. Inject at the Top Level

```kotlin
@Composable
fun FeatureScreen() {
    // Inject here
    val viewModel = koinViewModel<FeatureViewModel>()
    val repository = koinInject<FeatureRepository>()

    // Pass down to children
    FeatureContent(
        state = viewModel.state,
        onAction = viewModel::handleAction
    )
}
```

### 2. Keep Child Composables Pure

```kotlin
// Pure - receives all data as parameters
@Composable
fun UserCard(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // No injection here
}
```

### 3. Use ViewModel for Complex State

```kotlin
// Complex state management in ViewModel
@KoinViewModel
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {
    var query by mutableStateOf("")
        private set

    private val _results = MutableStateFlow<List<Result>>(emptyList())
    val results = _results.asStateFlow()

    fun updateQuery(newQuery: String) {
        query = newQuery
        viewModelScope.launch {
            _results.value = searchRepository.search(newQuery)
        }
    }
}
```

### 4. Avoid Injection in Loops

```kotlin
@Composable
fun UserList(userIds: List<String>) {
    // Inject once outside the loop
    val repository = koinInject<UserRepository>()

    LazyColumn {
        items(userIds) { userId ->
            // Don't inject inside items!
            UserCard(userId, repository)
        }
    }
}
```

## Next Steps

- **[ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel)** - ViewModel APIs
- **[Dynamic Modules](/docs/reference/koin-compose/compose-modules)** - Module loading/unloading
- **[Testing](/docs/reference/koin-compose/compose-testing)** - Testing Composables
