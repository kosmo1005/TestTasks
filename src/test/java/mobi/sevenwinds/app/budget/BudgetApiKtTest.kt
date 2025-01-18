package mobi.sevenwinds.app.budget

import io.restassured.RestAssured
import mobi.sevenwinds.common.ServerTest
import mobi.sevenwinds.common.jsonBody
import mobi.sevenwinds.common.toResponse
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BudgetApiKtTest : ServerTest() {

    @BeforeEach
    internal fun setUp() {
        transaction { BudgetTable.deleteAll() }
    }

    @Test
    fun testBudgetPagination() {
        addRecord(BudgetRecordReq(2020, 5, 10, BudgetType.Приход,null))
        addRecord(BudgetRecordReq(2020, 5, 5, BudgetType.Приход,null))
        addRecord(BudgetRecordReq(2020, 5, 20, BudgetType.Приход,null))
        addRecord(BudgetRecordReq(2020, 5, 30, BudgetType.Приход,null))
        addRecord(BudgetRecordReq(2020, 5, 40, BudgetType.Приход,null))
        addRecord(BudgetRecordReq(2030, 1, 1, BudgetType.Расход,null))

        RestAssured.given()
            .queryParam("limit", 3)
            .queryParam("offset", 1)
            .get("/budget/year/2020/stats")
            .toResponse<BudgetYearStatsResponse>().let { response ->
                println("${response.total} / ${response.items} / ${response.totalByType}")

                Assert.assertEquals(3, response.total)
                Assert.assertEquals(3, response.items.size)
                Assert.assertEquals(55, response.totalByType[BudgetType.Приход.name])
            }
    }

    @Test
    fun testStatsSortOrder() {
        addRecord(BudgetRecordReq(2020, 5, 100, BudgetType.Приход,null))
        addRecord(BudgetRecordReq(2020, 1, 5, BudgetType.Приход,null))
        addRecord(BudgetRecordReq(2020, 5, 50, BudgetType.Приход,null))
        addRecord(BudgetRecordReq(2020, 1, 30, BudgetType.Приход,null))
        addRecord(BudgetRecordReq(2020, 5, 400, BudgetType.Приход,null))

        // expected sort order - month ascending, amount descending

        RestAssured.given()
            .get("/budget/year/2020/stats?limit=100&offset=0")
            .toResponse<BudgetYearStatsResponse>().let { response ->
                val sortedItems = response.items
                    .sortedWith(compareBy({ it.month }, { -it.amount }))

                println(sortedItems)

                Assert.assertEquals(30, sortedItems[0].amount)
                Assert.assertEquals(5, sortedItems[1].amount)
                Assert.assertEquals(400, sortedItems[2].amount)
                Assert.assertEquals(100, sortedItems[3].amount)
                Assert.assertEquals(50, sortedItems[4].amount)
            }
    }

    @Test
    fun testInvalidMonthValues() {
        RestAssured.given()
            .jsonBody(BudgetRecordReq(2020, -5, 5, BudgetType.Приход,null))
            .post("/budget/add")
            .then().statusCode(400)

        RestAssured.given()
            .jsonBody(BudgetRecordReq(2020, 15, 5, BudgetType.Приход,null))
            .post("/budget/add")
            .then().statusCode(400)
    }

    private fun addRecord(record: BudgetRecordReq) {
        RestAssured.given()
            .jsonBody(record)
            .post("/budget/add")
            .toResponse<BudgetRecordReq>().let { response ->
                Assert.assertEquals(record, response)
            }
    }
}