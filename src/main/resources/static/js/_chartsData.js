var pieChartData = {
	datasets: [{
		data: [
			randomScalingFactor(),
			randomScalingFactor(),
			randomScalingFactor(),
			randomScalingFactor(),
			randomScalingFactor(),
		],
		backgroundColor: [
			window.chartColors.red,
			window.chartColors.orange,
			window.chartColors.yellow,
			window.chartColors.green,
			window.chartColors.blue,
		],
		label: 'Dataset 1'
	}],
	labels: [
		'Red',
		'Orange',
		'Yellow',
		'Green',
		'Blue'
	]
};
