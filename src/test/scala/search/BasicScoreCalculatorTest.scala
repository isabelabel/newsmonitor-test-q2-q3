package search

import org.jsoup.Jsoup
import org.scalatest.{FlatSpec, Matchers}

class BasicScoreCalculatorTest extends FlatSpec with Matchers with BasicScoreCalculator {

  it should "return the text length of a node with a text of length 14 and with a child that has text" in {
    val sample = "<div class=\"a\">Text length 14<div class=\"a1\">Text child other text length</div></div>"
    val divA = Jsoup.parse(sample).select("div.a").first()

    assert(textLength(divA) === 14)
  }

  it should "return the text length of a node without a text but with a child that has text" in {
    val sample = "<div class=\"a\"><br><div class=\"a1\">Text child other text length</div></div>"
    val divA = Jsoup.parse(sample).select("div.a").first()

    assert(textLength(divA) === 0)
  }

  it should "calc the score of a node with text length 4 when it has not a child" in {
    val sample = "<div class=\"a\">Text</div>"
    val divA = Jsoup.parse(sample).select("div.a").first()

    assert(calcScore(divA) === 4)
  }

  it should "calc the score of a node with text length 4 when it has one child " +
    "with text length 2" in {
    val sample = "<div class=\"a\">Text<div>dd</div></div>"
    val divA = Jsoup.parse(sample).select("div.a").first()

    assert(calcScore(divA) === 5)
  }

  it should "calc the score of a node with text length 4 when it has 2 child" +
    "first child text length is 2 and second child text length is 1" in {
    val sample = "<div class=\"a\">Text<div>dd</div><div>o</div></div>"
    val divA = Jsoup.parse(sample).select("div.a").first()

    assert(calcScore(divA) === 5.5)
  }

  it should "calc the score of a node with text length 4 when it has a child " +
    "with text length 1 and that has another child with text length 2" in {
    val sample = "<div class=\"a\">Text<div>b<div>cc</div></div></div>"
    val divA = Jsoup.parse(sample).select("div.a").first()

    assert(calcScore(divA) === 5)
  }

  it should "calc the score of a node child when it has not a child" in {
    val sample = "<div class=\"a\">Text</div>"
    val divA = Jsoup.parse(sample).select("div.a").first()

    assert(childrenScore(divA) === 0)
  }

  it should "calc the score of a node child when it has one child with a text length of 2 and " +
    "without a child" in {
    val sample = "<div class=\"a\">Text<div>bb</div></div>"
    val divA = Jsoup.parse(sample).select("div.a").first()

    assert(childrenScore(divA) === 2)
  }

  it should "calc the score of a node child when it has one child with a text length of 2 and " +
    "that child has another child with a text length of 2" in {
    val sample = "<div class=\"a\">Text<div>bb <div>cc</div></div></div>"
    val divA = Jsoup.parse(sample).select("div.a").first()

    assert(childrenScore(divA) === 3)
  }

  it should "calc the score of a node child when it has two child " +
    "first with length 1 and second with length 1" in {
    val sample = "<div class=\"a\">Text<div>b</div><div>c </div></div>"
    val divA = Jsoup.parse(sample).select("div.a").first()

    assert(childrenScore(divA) === 2)
  }
}
