<template>
    <v-card >
        <Radar :data="computeData" :options="options" style="radarStyle"    />
    </v-card>
</template>

<script lang="ts">
import {
    Chart as ChartJS,
    RadialLinearScale,
    PointElement,
    LineElement,
    Filler,
    Tooltip,
    Legend
} from 'chart.js'
import { Radar } from 'vue-chartjs'

ChartJS.register(
    RadialLinearScale,
    PointElement,
    LineElement,
    Filler,
    Tooltip,
    Legend
)

export default {
    name: 'radar-component',
    components: {
        Radar
    },
    data() {
        return {
            options: {
                responsive: true,
                maintainAspectRatio: false
            },
        }
    },
    computed: {
        computeData() {
            let labelsArray = []
            let gameSuggestionsSimilarityArray = []
            let optimalGameSuggestionsSimilarityArray = [];

            let firstDataSetObject = {};
            let secondDataSetObject = {};

            // Combine the two arrays into one using the spread operator
            let gameSuggestions = this.$store.state.gameSuggestions;
            let optimalGameSuggestions = this.$store.state.optimalGameSuggestions;

            // Merge arrays and extract unique names
            let mergedArray = [...gameSuggestions, ...optimalGameSuggestions];
            let uniqueNamesSet = new Set();
            mergedArray.forEach(item => uniqueNamesSet.add(item.name));

            if (mergedArray.length > 0) {

                labelsArray = []
                labelsArray = Array.from(uniqueNamesSet);

                // Empty arrays for similarity scores
                gameSuggestionsSimilarityArray = [];
                optimalGameSuggestionsSimilarityArray = [];

                labelsArray.forEach(name => {
                    let gameSuggestionItem = gameSuggestions.find(item => item.name === name);
                    let optimalGameSuggestionItem = optimalGameSuggestions.find(item => item.name === name);
                    if (gameSuggestionItem) {
                        gameSuggestionsSimilarityArray.push(gameSuggestionItem.similarity_score);
                    } else {
                        gameSuggestionsSimilarityArray.push(0); // Default value if similarity score not found
                    }
                    if (optimalGameSuggestionItem) {
                        optimalGameSuggestionsSimilarityArray.push(optimalGameSuggestionItem.similarity_score);
                    } else {
                        optimalGameSuggestionsSimilarityArray.push(0); // Default value if similarity score not found
                    }
                });

                firstDataSetObject = {
                    label: 'Optimal Suggestions',
                    backgroundColor: 'rgba(255,99,132,0.2)',
                    borderColor: 'rgba(255,99,132,1)',
                    pointBackgroundColor: 'rgba(255,99,132,1)',
                    pointBorderColor: '#fff',
                    pointHoverBackgroundColor: '#fff',
                    pointHoverBorderColor: 'rgba(255,99,132,1)',
                    data: optimalGameSuggestionsSimilarityArray
                }

                secondDataSetObject = {
                    label: 'User Chosen Attributes',
                    backgroundColor: 'rgba(179,181,198,0.2)',
                    borderColor: 'rgba(179,181,198,1)',
                    pointBackgroundColor: 'rgba(179,181,198,1)',
                    pointBorderColor: '#fff',
                    pointHoverBackgroundColor: '#fff',
                    pointHoverBorderColor: 'rgba(179,181,198,1)',
                    data: gameSuggestionsSimilarityArray
                }

                let data = {
                    labels: labelsArray,
                    datasets: [firstDataSetObject, secondDataSetObject]
                }
                return data;
            } 
            // else if (mergedArray.length > 30) {
            //     let optimalGameSuggestions = this.$store.state.optimalGameSuggestions;
            //     // Extract unique names and create nameArray
            //     uniqueNamesSet = new Set();
            //     optimalGameSuggestions.forEach(item => uniqueNamesSet.add(item.name));
            //     labelsArray = []
            //     labelsArray = Array.from(uniqueNamesSet);

            //     // Create optimalGameSuggestionsSimilarityArray
            //     optimalGameSuggestionsSimilarityArray = [];
            //     labelsArray.forEach(name => {
            //         const item = optimalGameSuggestions.find(item => item.name === name);
            //         if (item) {
            //             optimalGameSuggestionsSimilarityArray.push(item.similarity_score);
            //         } else {
            //             optimalGameSuggestionsSimilarityArray.push(0); // Default value if similarity score not found
            //         }
            //     });
            //     firstDataSetObject = {},
            //         firstDataSetObject = {
            //             label: 'Optimal Suggestions',
            //             backgroundColor: 'rgba(179,181,198,0.2)',
            //             borderColor: 'rgba(179,181,198,1)',
            //             pointBackgroundColor: 'rgba(179,181,198,1)',
            //             pointBorderColor: '#fff',
            //             pointHoverBackgroundColor: '#fff',
            //             pointHoverBorderColor: 'rgba(179,181,198,1)',
            //             data: optimalGameSuggestionsSimilarityArray
            //         }

            //     data = {
            //         labels: labelsArray,
            //         datasets: [firstDataSetObject]
            //     }
            //     return data;
            // } 
            else {
               let data = {
                    labels: [
                        'No Data',
                    ],
                    datasets: [
                        {
                            label: 'No Data',
                            backgroundColor: 'rgba(179,181,198,0.2)',
                            borderColor: 'rgba(179,181,198,1)',
                            pointBackgroundColor: 'rgba(179,181,198,1)',
                            pointBorderColor: '#fff',
                            pointHoverBackgroundColor: '#fff',
                            pointHoverBorderColor: 'rgba(179,181,198,1)',
                            data: [0]
                        },
                        {
                            label: 'No Data',
                            backgroundColor: 'rgba(255,99,132,0.2)',
                            borderColor: 'rgba(255,99,132,1)',
                            pointBackgroundColor: 'rgba(255,99,132,1)',
                            pointBorderColor: '#fff',
                            pointHoverBackgroundColor: '#fff',
                            pointHoverBorderColor: 'rgba(255,99,132,1)',
                            data: [0]
                        }
                    ]
                }
                return data;
            }
        }
    }
}
</script>

<style scoped>
/* Custom styles for the radar chart canvas */
canvas {
    width: 600px;
    /* Adjust the width as needed */
    height: 600px;
    /* Adjust the height as needed */
}
radarStyle {
    padding: 60px;
}
</style>