/*
 * Copyright © 2022 Camunda Services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.dmn

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DecisionTableTest extends AnyFlatSpec with Matchers with DecisionTest {

  lazy val discountDecision = parse("/decisiontable/discount.dmn")
  lazy val discountWithDefaultOutputDecision = parse(
    "/decisiontable/discount_default-output.dmn")

  lazy val adjustmentsDecision = parse("/decisiontable/adjustments.dmn")
  lazy val adjustmentsWithDefaultOutputDecision = parse(
    "/decisiontable/adjustments_default-output.dmn")
  lazy val adjustmentsWithEmptyOutputNameDecision =
    getClass.getResourceAsStream(
      "/decisiontable/adjustments_empty_output_name.dmn")

  lazy val routingRulesDecision = parse("/decisiontable/routingRules.dmn")
  lazy val holidaysDecision = parse("/decisiontable/holidays_output_order.dmn")

  "A decision table with single output" should "return single value" in {
    eval(discountDecision,
         "discount",
         Map("customer" -> "Business", "orderSize" -> 7)) should be(0.1)
  }

  it should "return value list" in {
    eval(holidaysDecision, "holidays", Map("age" -> 58, "yearsOfService" -> 31)) should be(
      List(22, 5, 3))
  }

  it should "return null if no rule match" in {
    eval(discountDecision,
         "discount",
         Map("customer" -> "Something else", "orderSize" -> 9)) should be(None)
  }

  it should "return the default-output if no rule match" in {
    eval(discountWithDefaultOutputDecision,
         "discount",
         Map("customer" -> "Something else", "orderSize" -> 9)) should be(0.05)
  }

  "A decision table with multiple outputs" should "return single values" in {
    val context = Map("customer" -> "Business", "orderSize" -> 7)

    eval(adjustmentsDecision, "adjustments", context) should be(
      Map("discount" -> 0.1, "shipping" -> "Air"))
  }

  it should "return value list" in {
    val context =
      Map("age" -> 25, "riskCategory" -> "MEDIUM", "deptReview" -> true)

    eval(routingRulesDecision, "routingRules", context) should be(
      List(
        Map("routing" -> "REFER",
            "reviewLevel" -> "LEVEL 2",
            "reason" -> "Applicant under dept review"),
        Map("routing" -> "ACCEPT",
            "reviewLevel" -> "NONE",
            "reason" -> "Acceptable")
      ))
  }

  it should "return null if no rule match" in {
    val context = Map("customer" -> "Something else", "orderSize" -> 9)

    eval(adjustmentsDecision, "adjustments", context) should be(None)
  }

  it should "return the default-output if no rule match" in {
    val context = Map("customer" -> "Something else", "orderSize" -> 9)

    eval(adjustmentsWithDefaultOutputDecision, "adjustments", context) should be(
      Map("discount" -> 0.05, "shipping" -> "Ground"))
  }

  it should "fail if an output name is empty" in {
    val result = engine.parse(adjustmentsWithEmptyOutputNameDecision)

    result.isLeft should be(true)
    result.left.map(
      _.message should be("no output name defined for `Discount`"))
  }
}
