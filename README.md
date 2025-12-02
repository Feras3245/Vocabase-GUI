# Vocabase (GUI/WebApp Edition)
Click [here](https://github.com/Feras3245/Vocabase-GUI/tree/main?tab=readme-ov-file#demo) to skip to the Demo
## About

This project was a GUI remake of my previous CLI python tool to scrape German term data from the popular online dictionary Linguee.com. This version is a Spring boot based web app with Vue 3 frontend that does the exact same function but scrapes terms from verben.de instead of linguee.com.

Features include:

- Search function that allows you to search for German term words that you want to scrape
- Ability to edit scraped result (definitions, prepositions, synonyms, antonyms, etc.)
- saving your scraped result in a Notion database page in your Notion workspace
- export Notion database of scraped words to Anki
- Dark/Light theme

## Dependencies

### Backend

- Java 24+
- Spring Boot 3.4.5
- JSoup 1.20

### Frontend

- Vue 3 with TypeScript

### Usage Instructions

- You will need a [Notion Internal API integration](https://developers.notion.com/docs/create-a-notion-integration#getting-started) to your Notion workspace.
- Create a page named `Vocabase` in your Notion workspace and [connect it with your Notion API integration](https://developers.notion.com/docs/create-a-notion-integration#give-your-integration-page-permissions). Make sure to give it full permissions.
- Inside the `Vocabase` page create a collection page and name it anything you want (I will use `Collection 1`) and create inside of the `Collcetion 1` page a group page called `Group 1`.
- The `Group 1` page must have 4 full page databases:
    - `Verbs`:
        - `Verb`: Title
        - `Definitions`: Text
        - `Synonyms`: Text
        - `Antonyms`: Text
        - `Prepositions`: Multi-Select
        - `PPII`: Text
        - `Preterite`: Text
        - `Tags`: Multi-Select
        - `Examples`: Text
        - `Niveau`: Select
    - `Nouns`:
        - `Noun`: Title
        - `Article`: Select
        - `Plural`: Text
        - `Definitions`: Text
        - `Synonyms`: Text
        - `Antonyms`: Text
        - `Examples`: Text
        - `Niveau`: Select
    - `Adjectives`:
        - `Adjective`: Title
        - `Comparative`: Text
        - `Superlative`: Text
        - `Definitions`: Text
        - `Synonyms`: Text
        - `Antonyms`: Text
        - `Examples`: Text
        - `Niveau`: Select
- In Notion, navigate to the root `Vocabase` page and copy its ID. You can find its ID:
    - Navigate to the root `Vocabase` page
    - On the top right corner of Notion, click the share button.
    - Click Copy link
    - If the link you copied is `https://www.notion.so/Vocabase-745d95ca814d412sbad9d175f3a65f3f?source=copy_link` then the ID is `745d95ca814d412sbad9d175f3a65f3f`
- Open the `application.properties` file found in the same directory as `vocabase.jar` and Notion API secret along with the root ID of your `Vocabase` page:
    
    ```yaml
    notion.secret.key=YOUR_NOTION_API_SECRET
    vocabase.root.id=YOUR_VOCABASE_PAGE_ROOT_ID
    server.port=3131
    ```
    
    - The default server port is `3131` but you can change it to whatever you like
- Open A terminal in the same folder as `vocabase.jar` and launch the server by typing:
    
    ```bash
    java -jar vocabase.jar
    ```
    
- In your browser go to `http://localhost:3131` and open the settings menu:
    <img src="https://github.com/Feras3245/Vocabase-GUI/blob/main/screentshot-1.png?raw=true" width="680">
    - Make sure a Collection and Group are selected
- Search for any German term you would like to scrape, edit the returned results as you like and then click `Send To Notion` when you are ready:
    <img src="https://github.com/Feras3245/Vocabase-GUI/blob/main/screenshot-2.png?raw=true" width="680">
- If you did everything correctly you will see the term is successfully stored in your Notion database:
    <img src="https://github.com/Feras3245/Vocabase-GUI/blob/main/screenshot-3.png?raw=true" width="680">
- You can create other collections and groups as you like (e.g. `Collection 2`, `Collection 3`, `Group 1`, `Group 2`, etc.) and switch between them as you please from the Settings menu.
- **Note:** currently there is no automated way to create groups and collections, for the forceable future you will have to do so manually.

## Demo
![demo](https://github.com/Feras3245/Vocabase-GUI/blob/main/demo.gif?raw=true)
