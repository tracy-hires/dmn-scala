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

import org.camunda.dmn.DmnEngine._
import java.time.LocalDate

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RelationTest extends AnyFlatSpec with Matchers with DecisionTest {

  lazy val applicantData = parse("/relation/ApplicantData.dmn")

  "A relation" should "return a list of contexts" in {
    eval(applicantData, "applicantData", Map()) should be(
      Map("CreditHistory" -> List(
        Map(
          "recordDate" -> LocalDate.parse("2008-03-12"),
          "event" -> "home mortgage",
          "weight" -> 100
        ),
        Map(
          "recordDate" -> LocalDate.parse("2011-04-01"),
          "event" -> "foreclosure warning",
          "weight" -> 150
        )
      )))
  }

}
