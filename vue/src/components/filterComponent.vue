<template>
    <v-row justify="center">
        <v-dialog v-model="dialog" persistent max-width="700px">
            <template v-slot:activator="{ on, attrs }">
                <v-btn class="buttonStyling" dark rounded raised large elevation="10" v-bind="attrs" v-on="on"
                    @click="formatOptions">
                    Edit/Request Filters

                </v-btn>
            </template>
            <v-card>
                <v-card-title>
                    <span class="text-h5" style="color:blueviolet">Edit or request attributes for similar
                        suggestions</span>
                </v-card-title>
                <v-card-text>
                    <v-container>
                        <v-row>
                            <v-col cols="12" sm="6" md="3">
                                <v-switch v-model="name" label="Name" color="red" hide-details></v-switch>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-switch v-model="release_year" label="Release Year" color="blue darken-3"
                                    hide-details></v-switch>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-switch v-model="price" label="Price" color="green" hide-details></v-switch>
                            </v-col>
                            <v-col cols="12" sm="6" md="3">
                                <v-switch v-model="description" label="Description" color="deep-orange darken-2"
                                    hide-details></v-switch>
                            </v-col>
                            <v-col cols="12" sm="6" md="4">
                                <v-switch v-model="windows" label="Windows" color="orange" hide-details></v-switch>
                            </v-col>
                            <v-col cols="12" sm="6" md="4">
                                <v-switch v-model="linux" label="Linux" color="indigo" hide-details></v-switch>
                            </v-col>
                            <v-col cols="12" sm="6" md="4">
                                <v-switch v-model="mac" label="Mac" color="light-blue" hide-details></v-switch>
                            </v-col>
                            <v-col cols="12" sm="6">
                                <v-select v-model="chosenDevelopers"
                                    :items="$store.state.chosenGameForSuggestion.developers"
                                    :menu-props="{ maxHeight: '400' }" label="Developers" multiple
                                    hint="Select your developer(s)" persistent-hint></v-select>
                            </v-col>
                            <v-col cols="12" sm="6">
                                <v-select v-model="chosenPublishers"
                                    :items="$store.state.chosenGameForSuggestion.publishers"
                                    :menu-props="{ maxHeight: '400' }" label="Publishers" multiple
                                    hint="Select your publisher(s)" persistent-hint></v-select>
                            </v-col>
                            <v-col cols="12" sm="6">
                                <v-select v-model="chosenCategories" :items="categories"
                                    :menu-props="{ maxHeight: '400' }" label="Categories" multiple
                                    hint="Select your categories" persistent-hint></v-select>
                            </v-col>
                            <v-col cols="12" sm="6">
                                <v-select v-model="chosenGenres" :items="genres" :menu-props="{ maxHeight: '400' }"
                                    label="Genres" multiple hint="Select your genres" persistent-hint></v-select>
                            </v-col>
                            <v-col cols="12">
                                <div>Select your tags</div>
                                <v-autocomplete v-model="chosenTags" :items="tags" filled small-chips chips label="Tags"
                                    multiple>
                                    <template v-slot:selection="data">
                                        <v-chip v-bind="data.attrs" :input-value="data.selected" close
                                            @click="data.select" @click:close="remove(data.item)" small
                                            :color="randomColor">
                                            {{ data.item }}
                                        </v-chip>
                                    </template></v-autocomplete>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>
                <v-card-actions>
                    <v-btn color="red darken-1" text @click="closeAndReset()">
                        Close
                    </v-btn>
                    <v-spacer></v-spacer>
                    <v-btn color="blue darken-1" :loading="loading" :disabled="loading" @click="applyFilters()">
                        <template v-slot:loader>
                            <span>Loading...</span>
                        </template>
                        Apply Attributes
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-row>
</template>

<script>
import SteamGamesService from '@/services/SteamGamesService';

export default {
    name: "filter-component",
    created() {
        this.chosenCategories = this.$store.state.chosenGameForSuggestion.categories;
        this.chosenGenres = this.$store.state.chosenGameForSuggestion.genres;
        this.chosenTags = this.$store.state.chosenGameForSuggestion.tags;
    },
    data() {
        return {
            dialog: false,
            name: true,
            price: false,
            release_year: false,
            windows: false,
            linux: false,
            mac: false,
            loading: false,
            chosenGame: {},
            chosenDevelopers: [],
            chosenPublishers: [],
            categories: ["Full controller support", "Commentary available", "Steam Workshop", "Includes Source SDK", "SteamVR Collectibles", "Steam Cloud", "VR Supported", "Mods", "Steam Achievements", "Single-player", "Includes level editor", "Co-op", "LAN PvP", "PvP", "In-App Purchases", "Cross-Platform Multiplayer", "Mods (require HL2)", "Captions available", "Remote Play on Tablet", "VR Only", "Stats", "Steam Leaderboards", "Remote Play Together", "Steam Turn Notifications", "Remote Play on Phone", "Tracked Motion Controller Support", "Steam Trading Cards", "HDR available", "Online PvP", "Remote Play on TV", "LAN Co-op", "Shared/Split Screen", "Online Co-op", "VR Support", "Tracked Controller Support", "MMO", "Partial Controller Support", "Multi-player", "Valve Anti-Cheat enabled", "Shared/Split Screen Co-op", "Shared/Split Screen PvP"],
            chosenCategories: [],
            genres: ["Violent", "Gore", "Education", "Accounting", "Animation & Modeling", "Tutorial", "Software Training", "Free to Play", "Strategy", "Documentary", "Early Access", "Movie", "Audio Production", "Racing", "Photo Editing", "Short", "Utilities", "Sports", "Casual", "Sexual Content", "Nudity", "Design & Illustration", "Adventure", "Indie", "Action", "Web Publishing", "Simulation", "RPG", "Video Production", "Game Development", "Massively Multiplayer", "360 Video", "Episodic"],
            chosenGenres: [],
            tags: ["Mouse only", "Family Friendly", "Bowling", "Emotional", "Illuminati", "Farming", "1990's", "Top-Down", "Programming", "Trading Card Game", "RPGMaker", "Gore", "Team-Based", "Replay Value", "Education", "Co-op Campaign", "Minimalist", "Animation & Modeling", "Tennis", "America", "Comedy", "Local Multiplayer", "War", "e-sports", "Match 3", "Walking Simulator", "Software Training", "Romance", "Creature Collector", "Voxel", "Stealth", "Nudity", "Destruction", "Point & Click", "Dungeon Crawler", "Realistic", "Strategy", "Sequel", "Political Sim", "GameMaker", "Action-Adventure", "Wholesome", "Lara Croft", "Story Rich", "Satire", "Co-op", "Action RPG", "Based On A Novel", "Singleplayer", "NSFW", "2.5D", "Physics", "Relaxing", "Instrumental Music", "Clicker", "Collectathon", "Arcade", "8-bit Music", "Batman", "Cult Classic", "Gaming", "Idler", "Early Access", "CRPG", "Pinball", "Great Soundtrack", "Isometric", "Cats", "Controller", "Experience", "Party-Based RPG", "Escape Room", "Faith", "Narration", "Experimental", "Local Co-Op", "Snow", "Mechs", "VR Only", "Rome", "Sniper", "Classic", "Typing", "Hero Shooter", "Psychological", "Rock Music", "Twin Stick Shooter", "Beat 'em up", "Medieval", "Fishing", "Life Sim", "Chess", "Mod", "Hockey", "Character Customization", "Epic", "Investigation", "Diplomacy", "Arena Shooter", "Warhammer 40K", "Pool", "Hardware", "Transportation", "Offroad", "Parkour", "Time Manipulation", "Gambling", "Motocross", "Photo Editing", "Text-Based", "MMORPG", "God Game", "Trivia", "Zombies", "Nostalgia", "Dog", "Asymmetric VR", "MOBA", "Cartoony", "Sokoban", "Magic", "Short", "Agriculture", "2D", "Utilities", "Detective", "Crime", "Horses", "World War I", "Automobile Sim", "Battle Royale", "Party Game", "Dynamic Narration", "Logic", "Party", "Foreign", "Pirates", "Vehicular Combat", "Old School", "Baseball", "Immersive Sim", "Tile-Matching", "World War II", "Sports", "Skateboarding", "Top-Down Shooter", "Tactical RPG", "Class-Based", "Archery", "Combat Racing", "Flight", "Casual", "Benchmark", "Unforgiving", "RTS", "Dark Comedy", "Lemmings", "Strategy RPG", "3D Platformer", "Cute", "Anime", "Noir", "Side Scroller", "Colony Sim", "Design & Illustration", "Management", "Adventure", "Electronic", "Crowdfunded", "Spectacle fighter", "Indie", "Competitive", "Hidden Object", "Real-Time", "Bullet Time", "Cold War", "Cinematic", "Character Action Game", "Spelling", "PvE", "Mars", "Nonlinear", "Platformer", "3D", "Real Time Tactics", "Golf", "Shooter", "6DOF", "Driving", "VR", "Military", "Interactive Fiction", "LEGO", "Simulation", "Medical Sim", "Psychedelic", "Capitalism", "City Builder", "Rugby", "Philosophical", "Vikings", "Hunting", "RPG", "Narrative", "Dinosaurs", "Turn-Based Combat", "Split Screen", "Feature Film", "JRPG", "Hand-drawn", "Memes", "Video Production", "Game Development", "Massively Multiplayer", "Dragons", "Asynchronous Multiplayer", "Atmospheric", "Jet", "360 Video", "Score Attack", "Economy", "Board Game", "Time Management", "Cyberpunk", "Soundtrack", "Dystopian", "Hack and Slash", "Immersive", "Shoot 'Em Up", "Survival Horror", "Dating Sim", "Roguelike Deckbuilder", "Violent", "Combat", "Sci-fi", "Music-Based Procedural Generation", "Superhero", "Naval Combat", "Football (Soccer)", "Funny", "Base-Building", "Hobby Sim", "Wrestling", "Cricket", "Extraction Shooter", "Steampunk", "Time Travel", "Tutorial", "Open World Survival Craft", "Coding", "Post-apocalyptic", "Western", "Transhumanism", "Electronic Music", "Basketball", "Hex Grid", "Job Simulator", "Dark Fantasy", "Word Game", "Tower Defense", "Space", "Parody", "Remake", "Addictive", "Card Battler", "Free to Play", "Real-Time with Pause", "Social Deduction", "ATV", "Tabletop", "On-Rails Shooter", "Bullet Hell", "Tanks", "Hentai", "Third-Person Shooter", "Exploration", "Spaceships", "Procedural Generation", "Farming Sim", "Swordplay", "Silent Protagonist", "Documentary", "Card Game", "Conspiracy", "Comic Book", "Automation", "Mini Golf", "Lovecraftian", "Deckbuilding", "Kickstarter", "Grid-Based Movement", "4X", "PvP", "Loot", "Musou", "First-Person", "Well-Written", "Submarine", "Heist", "Online Co-Op", "Dark Humor", "Rogue-lite", "Modern", "Movie", "Skiing", "Underground", "Snowboarding", "Underwater", "Mature", "Outbreak Sim", "Werewolves", "Linear", "Audio Production", "Cartoon", "Gun Customization", "Naval", "Shop Keeper", "Auto Battler", "Thriller", "Voice Control", "Difficult", "Solitaire", "Space Sim", "Jump Scare", "1980s", "Dungeons & Dragons", "Otome", "Sailing", "Multiplayer", "Third Person", "Supernatural", "Racing", "Looter Shooter", "Football (American)", "Beautiful", "Boxing", "Pixel Graphics", "Lore-Rich", "Mahjong", "Quick-Time Events", "Nature", "Assassin", "Alternate History", "Politics", "Abstract", "Blood", "Steam Machine", "Inventory Management", "Level Editor", "Turn-Based Tactics", "Music", "Multiple Endings", "Aliens", "Choose Your Own Adventure", "Volleyball", "Birds", "Tactical", "Minigames", "Choices Matter", "Martial Arts", "Souls-like", "Reboot", "Boss Rush", "Resource Management", "Precision Platformer", "Dark", "Grand Strategy", "Surreal", "Metroidvania", "Traditional Roguelike", "FMV", "Visual Novel", "Fighting", "Artificial Intelligence", "Fast-Paced", "Cozy", "Games Workshop", "Drama", "Gothic", "Mystery", "Sandbox", "Cooking", "Moddable", "TrackIR", "Female Protagonist", "BMX", "Puzzle-Platformer", "Sexual Content", "Building", "2D Platformer", "Rogue-like", "Intentionally Awkward Controls", "Fantasy", "Political", "4 Player Local", "Fox", "Time Attack", "Vampire", "Cycling", "Wargame", "Stylized", "Action", "Survival", "Motorbike", "Action Roguelike", "Web Publishing", "Trading", "Psychological Horror", "3D Vision", "Football", "Skating", "Villain Protagonist", "Mystery Dungeon", "Retro", "Touch-Friendly", "Puzzle", "Horror", "Runner", "Mining", "Open World", "Turn-Based Strategy", "Historical", "Ninja", "Roguevania", "Futuristic", "Bikes", "Masterpiece", "Perma Death", "Turn-Based", "3D Fighter", "Hacking", "Demons", "Crafting", "Science", "Ambient", "Colorful", "Robots", "Soccer", "Action RTS", "Rhythm", "2D Fighter", "Conversation", "Software", "FPS", "LGBTQ+", "Trains", "Mythology", "Episodic"],
            chosenTags: [],
        }
    },
    methods: {
        closeAndReset() {
            this.dialog = false;
        },
        formatOptions() {
            if (this.chosenGame.game_id != this.$store.state.chosenGameForSuggestion.game_id) {
                this.chosenGame = this.$store.state.chosenGameForSuggestion;
                this.chosenCategories = this.$store.state.chosenGameForSuggestion.categories;
                this.chosenGenres = this.$store.state.chosenGameForSuggestion.genres;
                this.chosenTags = this.$store.state.chosenGameForSuggestion.tags;
            }
        },
        remove(item) {
            const index = this.chosenTags.indexOf(item)
            if (index >= 0) this.chosenTags.splice(index, 1)
        },
        applyFilters() {
            this.loading = true;
            let filteredStrings = [];

            if (this.name) {
                filteredStrings.push(this.$store.state.chosenGameForSuggestion.name)
            }

            if (this.price) {
                let tempPrice = this.$store.state.chosenGameForSuggestion.price;
                filteredStrings.push(tempPrice.toString())
            }

            if (this.release_year) {
                let date = this.$store.state.chosenGameForSuggestion.release_date;
                filteredStrings.push(date.split(", ")[1])
            }

            if (this.windows) {
                filteredStrings.push("Windows")
            }

            if (this.linux) {
                filteredStrings.push("Linux")
            }

            if (this.mac) {
                filteredStrings.push("Mac")
            }

            if (this.chosenCategories.length > 0) {
                this.chosenCategories.forEach(category => {
                    filteredStrings.push(category);
                });
            }

            if (this.chosenGenres.length > 0) {
                this.chosenGenres.forEach(genre => {
                    filteredStrings.push(genre);
                });
            }

            if (this.chosenTags.length > 0) {
                this.chosenTags.forEach(tag => {
                    filteredStrings.push(tag);
                });
            }
            if (filteredStrings.length > 0) {
                let applyFilteredObject = {
                    filteredStrings: filteredStrings,
                    game_id: this.chosenGame.game_id
                }
                SteamGamesService.applyFilters(applyFilteredObject)
                    .then((response) => {
                        if (response.status == 201) {
                            this.$store.commit("SET_GAME_SUGGESTIONS", response.data.newGameSuggestions)
                            this.$store.commit("SET_OPTIMAL_GAME_SUGGESTIONS", response.data.optimalGameSuggestions)
                            this.loading = false;
                            this.dialog = false;
                        }
                    })
                    .catch((error) => {
                        const response = error.response;
                        if (response.status === 400 || response.status === 404) {
                            alert(error.response.data.message)
                        }
                        this.loading = false;
                        this.dialog = false;

                    });
            } else {
                alert("Please select filters/attributes")
                this.loading = false;
            }

        },
    },
    computed: {
        randomColor() {
            return 'blue'
        }
    },
    watch: {
        loader() {
            const l = this.loader
            this[l] = !this[l]

            setTimeout(() => (this[l] = false), 3000)

            this.loader = null
        },
    },
}
</script>

<style scoped>
.buttonStyling {
    color: blueviolet
}
</style>