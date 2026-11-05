import kotlin.test.assertEquals

fun check() {
    assertEquals("Hello, Alice!", databaseService.greet())
    assertEquals("Hello, Bob!", inMemoryService.greet())
    UserServiceTest().greetsTheUser()
    main()
}
