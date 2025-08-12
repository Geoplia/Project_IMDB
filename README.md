# 🎬 IMDB Desktop Manager

A modern Java Swing desktop application to manage movies, series, seasons, and episodes — fully styled with a modern dark theme.  
Supports advanced search, ratings, and multiple viewing modes (table & gallery).

## ✨ Features

- **Movie Management**
  - Add, edit, and rate movies
  - Toggle between table view & gallery view
  - Sortable columns with custom arrow icons
  - Rounded modern cards for movies with posters

- **Series Management**
  - Add series, seasons, and episodes
  - Expandable/collapsible tree view for seasons and episodes
  - Right-click context menus for quick actions

- **Search**
  - Filter by title, actor, or director
  - Minimum IMDb & user rating filters

- **Ratings**
  - Add ratings for movies & series from registered users


## 🖼 Screenshots

-Main screen / Movies Panel with movie info
<img width="1349" height="981" alt="Main" src="https://github.com/user-attachments/assets/630488c4-f6a0-411a-81e1-6a18a570c225" />

-Search Section
<img width="1333" height="554" alt="Search" src="https://github.com/user-attachments/assets/5749ffaf-dada-44d2-90f0-7a42c03c4d87" />

-Series view with Add Series option
<img width="1026" height="548" alt="Series" src="https://github.com/user-attachments/assets/97b06fba-7f6f-4efb-befc-326142967ebc" />

-**Run**
-App.java is the entry point.

-**Project Structure**
```
project
└───src
│   └───com
│       └───uop
│           └───imdb
│               │   App.java
│               │   UITheme.java
│               │
│               └───exceptions
│               │   │   DuplicateDataException.java
│               │   │   InvalidDataException.java
│               │   │   NotFoundException.java
│               │
│               └───model
│               │   └───enums
│               │   │   │   Gender.java
│               │   │   │   Genre.java
│               │   │   │   Race.java
│               │   │
│               │   │   Actor.java
│               │   │   Director.java
│               │   │   Episode.java
│               │   │   Movie.java
│               │   │   Person.java
│               │   │   Season.java
│               │   │   Series.java
│               │   │   User.java
│               │
│               └───storage
│               │   │   DataStore.java
│               │   │   FileParser.java
│               │
│               └───ui
│                   └───dialogs
│                   │   │   AddEpisodeDialog.java
│                   │   │   AddMovieDialog.java
│                   │   │   AddRatingDialog.java
│                   │   │   AddSeasonDialog.java
│                   │   │   AddSeriesDialog.java
│                   │   │   DirectorDetailsDialog.java
│                   │   │   MovieDetailsDialog.java
│                   │
│                   │   DarkTreeCellRenderer.java
│                   │   DirectorsPanel.java
│                   │   MainFrame.java
│                   │   MoviesPanel.java
│                   │   RoundedCard.java
│                   │   SearchPanel.java
│                   │   SeriesPanel.java
│                   │   WrapLayout.java
│
└───images
    │   placeholder.png

```

