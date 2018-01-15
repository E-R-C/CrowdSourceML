# What is it?
This is my undergraduate senior thesis project at Hendrix College. The goal of this project is to create a mobile application which can be used to identify buildings on the Hendrix college campus. This project also aims to be a demonstration of using [Binary Robust Independent Elementary Features](http://icwww.epfl.ch/~lepetit/papers/calonder_eccv10.pdf) along with [FAST](https://www.edwardrosten.com/work/fast.html) corner detection in an android application. This application as it stands is not domain specific and therefore can be used to identify anything from locations to office equipment. This may not work as well as already established methods of object detection, but that's the point! It's a research project!

# Required for Functionality Goals
- [x] Request a photo from the built-in camera app
- [x] Display the picture to the user
- [x] Create database with BRIEF patches
- [x] Create database with Locations and allow user to populate it
- [x] Create a way to record all trained images so far (Done via database)
- [x] Develop a way to filter FAST points (Distance to center, however, this should be revisited and use methods like Shi-thomassi filtering).
- [ ] Pick a photo from their gallery
- [ ] Train multiple images at a time
- [ ] Classify all trained images
- [ ] Display Database size
- [ ] Show user confusion matrix and how many patches were matched
# Stretch Goals
- [ ] Upload to Android Store
- [ ] Create trainers for other classification models (kNN)
- [ ] Display which Brief patches were used in the classification process (2 images side by side display)
