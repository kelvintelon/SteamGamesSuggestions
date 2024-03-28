<template>
    <v-card>
        <Bar :data="computeData" :options="options" />
    </v-card>
</template>

<script lang="ts">
import {
    Chart as ChartJS,
    Title,
    Tooltip,
    Legend,
    BarElement,
    CategoryScale,
    LinearScale
} from 'chart.js'
import { Bar } from 'vue-chartjs'

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

export default {
    name: 'bar-component',
    components: {
        Bar
    },
    data() {
        return {
            options: {
                responsive: true,
                maintainAspectRatio: false,
            }
        }
    },
    computed: {
        computeData() {
            let labelsArray = []
            let gameSuggestionsSimilarityArray = []
            let optimalGameSuggestionsSimilarityArray = [];

            let firstDataSetObject = {};
            let secondDataSetObject = {};

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

                let isThereOneDataSet = this.compareArrays(gameSuggestionsSimilarityArray, optimalGameSuggestionsSimilarityArray)

                firstDataSetObject = {
                    label: 'Optimal Attributes Suggestions',
                    backgroundColor: 'rgba(255,99,132,1)',
                    data: optimalGameSuggestionsSimilarityArray
                }

                secondDataSetObject = {
                    label: 'User Requested Attributes',
                    backgroundColor: 'rgba(179,181,198,1)',
                    data: gameSuggestionsSimilarityArray
                }

                if (isThereOneDataSet) {
                    let data = {
                        labels: labelsArray,
                        datasets: [firstDataSetObject]
                    }
                    return data;
                } else {
                    let data = {
                        labels: labelsArray,
                        datasets: [firstDataSetObject, secondDataSetObject]
                    }
                    return data;
                }

            }
            else {
                let data = {
                    labels: [
                        'No Data',
                    ],
                    datasets: [
                        {
                            label: 'No Data',
                            backgroundColor: 'rgba(179,181,198,0.2)',
                            data: [0]
                        },
                        {
                            label: 'No Data',
                            backgroundColor: 'rgba(255,99,132,0.2)',
                            data: [0]
                        }
                    ]
                }
                return data;
            }
        }
    },
    methods: {
        compareArrays(array1, array2) {
            if (array1.length !== array2.length) {
                return false; // Arrays have different lengths
            }

            for (let i = 0; i < array1.length; i++) {
                if (array1[i] !== array2[i]) {
                    return false; // Numbers at the same index are different
                }
            }

            return true; // Arrays are identical
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
</style>