var config1 = {
	type: 'pie',
	data: pieChartData,
	options: {
		responsive: true,
		legend: {
			position: 'top',
		},
		title: {
			display: true,
			text: 'Chart.js Pie Chart'
		}
	}
};
var config2 = {
	type: 'line',
	data: {
		labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
		datasets: [{
			label: 'My First dataset',
			backgroundColor: window.chartColors.red,
			borderColor: window.chartColors.red,
			data: [
				randomScalingFactor(),
				randomScalingFactor(),
				randomScalingFactor(),
				randomScalingFactor(),
				randomScalingFactor(),
				randomScalingFactor(),
				randomScalingFactor()
			],
			fill: false,
		}, {
			label: 'My Second dataset',
			fill: false,
			backgroundColor: window.chartColors.blue,
			borderColor: window.chartColors.blue,
			data: [
				randomScalingFactor(),
				randomScalingFactor(),
				randomScalingFactor(),
				randomScalingFactor(),
				randomScalingFactor(),
				randomScalingFactor(),
				randomScalingFactor()
			],
		}]
	},
	options: {
		responsive: true,
		title: {
			display: true,
			text: 'Chart.js Line Chart'
		},
		tooltips: {
			mode: 'index',
			intersect: false,
		},
		hover: {
			mode: 'nearest',
			intersect: true
		},
		scales: {
			xAxes: [{
				display: true,
				scaleLabel: {
					display: true,
					labelString: 'Month'
				}
			}],
			yAxes: [{
				display: true,
				scaleLabel: {
					display: true,
					labelString: 'Value'
				}
			}]
		}
	}
};
