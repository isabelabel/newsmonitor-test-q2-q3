## Explanation about the news body finder
1. Get the HTML body
2. Crate a list with all nodes present in the body, including all the child. This list is being created recursively,
3. The nodes that represent just the texts are removed
4. For each node in the created list the calc of the score is apply, creating a list where each element represent
the node and your score
5. The list created in the previously step is sorted, so first element will be the node with the biggest score
6. The first node of the sorted list is returned

* *It uses Jsoup to extract the nodes and all the DOM HTML manipulation*

## Explanation about the news web crawler

It uses akka, so each part of the process is represented by an Actor.
* [A] An actor responsible to download a link. Given one URL, he will make the requisition and return a message with the response data
* [B] An actor responsible to search and extract the most likely desired article inside a HTML
* [C] An actor responsible to extract all the links inside a HTML
* [D] An Actor that manage the crawling execution. According the follow logic:
  1. Call the [A] passing the start url
  2. When receive the HTML for the message [1], call the [B] passing the received HTML
  3. When receive the news node for the message [2], save this in a list of results and checks if it should continue the extraction (based on the level) or not
    3.1 If should not continue, because the level is already 0, it will wait until all the other actor finish their job
    and when their finish, return the list of results
    3.2 If should continue, the level is not 0, it will call the [C] passing the same HTML received in [2]
      3.2.1 When receive the list of links for the message in [3.2], call [A] passing each link
      3.2.2 Go to step [3]
      
* The main application it will receive the start url and the level, send the message to [D] to start the crawler.
It will wait for the result and then print all news (html body) found 

## How to Execute only the program that extract News most likely body

  - Execute: 
    - `sbt "run-main search.NewsBodyFinderApp \"<html>\""`
    - Where `<html>` is the text HTML that you want to test
  - Example `<html>` can be replace by:
  ```
    <html><body><div class="a">Title</div><div class="b">Subtitulo<div class="b-1">Intro<div class="b-1-1">Paragrafo1</div><div class="b-1-2">Paragrafo2</div></div></div></body></html>`.
  So you can execute `sbt "run-main search.NewsBodyFinderApp \"<html><body><div class="a">Title</div><div class="b">Subtitulo<div class="b-1">Intro<div class="b-1-1">Paragrafo1</div><div class="b-1-2">Paragrafo2</div></div></div></body></html>\"
  ```
  * *This HTML is the one inside `news_example.html`*

## How to Execute the program to crawl news page

  - Execute:
    - `sbt "run-main core.CrawlerApp <start_ulr> <levels>"` 
    - Where `<start_url>` is the url where the crawler has to start to look for the news and `<levels>` is the number that
  represents how deeply the crawler should go
  - Example:
    - `sbt "run-main core.CrawlerApp http://www.terra.com.br/ 0"`

## How to run the tests
  - Execute:
    - `sbt test`
