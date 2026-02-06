import { AfterViewInit, Component, ElementRef, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { CommonModule } from '@angular/common';

Chart.register(...registerables);

export interface ChartData {
  labels: string[];
  data: number[];
}

@Component({
  selector: 'app-report-graph',
  imports: [CommonModule],
  templateUrl: './report-graph.html',
  styleUrl: './report-graph.css',
})

export class ReportGraph implements AfterViewInit, OnChanges {
  @Input() title: string = '';
  @Input() xAxisLabel: string = '';
  @Input() yAxisLabel: string = '';
  @Input() chartData: ChartData = { labels: [], data: [] };
  @Input() sum: number = 0;
  @Input() average: number = 0;

  @ViewChild('chartCanvas', { static: false }) chartCanvas!: ElementRef<HTMLCanvasElement>;
  private chart?: Chart;

  ngAfterViewInit(): void {
    this.createChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Update chart if chartData or chartType changes
    if (this.chart && changes['chartData']) {
      this.updateChart();
    }
  }

  private createChart(): void {
    if (!this.chartCanvas) return;

    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: this.chartData.labels,
        datasets: [{
          label: this.yAxisLabel,
          data: this.chartData.data,
          backgroundColor: 'rgba(188, 108, 37, 0.7)', // #BC6C25 transparent
          borderColor: '#BC6C25',
          borderWidth: 2,
          hoverBackgroundColor: '#DDA15E',
          hoverBorderColor: '#BC6C25',
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
          legend: {
            display: false,
            position: 'top',
            labels: {
              color: '#606C38',
              font: {
                size: 14,
                weight: 'bold'
              }
            }
          },
          tooltip: {
            backgroundColor: '#606C38',
            titleColor: '#fff',
            bodyColor: '#fff',
            borderColor: '#BC6C25',
            borderWidth: 1
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: this.xAxisLabel,
              color: '#606C38',
              font: {
                size: 14,
                weight: 'bold'
              }
            },
            ticks: {
              color: '#606C38'
            },
            grid: {
              color: 'rgba(96, 108, 56, 0.1)'
            }
          },
          y: {
            title: {
              display: true,
              text: this.yAxisLabel,
              color: '#606C38',
              font: {
                size: 14,
                weight: 'bold'
              }
            },
            ticks: {
              color: '#606C38'
            },
            grid: {
              color: 'rgba(96, 108, 56, 0.1)'
            },
            beginAtZero: true
          }
        }
      }
    };

    this.chart = new Chart(ctx, config);
  }

  private updateChart(): void {
    if (!this.chart) return;

    this.chart.data.labels = this.chartData.labels;
    this.chart.data.datasets[0].data = this.chartData.data;
    this.chart.data.datasets[0].label = this.yAxisLabel;
    
    // Update axis labels
    const xScale = this.chart.options.scales?.['x'] as any;
    if (xScale?.title) {
      xScale.title.text = this.xAxisLabel;
    }
    const yScale = this.chart.options.scales?.['y'] as any;
    if (yScale?.title) {
      yScale.title.text = this.yAxisLabel;
    }

    this.chart.update();
  }

  ngOnDestroy(): void {
    if (this.chart) {
      this.chart.destroy();
    }
  }
}
