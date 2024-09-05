<script>
import * as echarts from 'echarts'
import merge from 'lodash.merge'

export default {
  // 折线图
  name: 'ChartLine',
  components: {},
  props: {
    xData: {
      type: Array,
      default: () => [],
    },
    legend: {
      type: [Array, String],
      default: () => [],
    },
    series: {
      type: Array,
      default: () => [],
    },
    // 自定义图表配置项，使用loadsh.merge(origin, option)和原有的配置进行覆盖合并
    // loadsh.merge: https://www.html.cn/doc/lodash/#_mergeobject-sources
    option: {
      type: Object,
      default: null,
    },
    bgLinearGradient: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {}
  },
  computed: {},
  watch: {
    series: {
      deep: true,
      handler() {
        this.$nextTick(() => {
          this.drawChart()
        })
      },
    },
  },
  created() {},
  mounted() {
    this.drawChart()
  },
  methods: {
    drawChart() {
      if (!(this.xData?.length && this.series?.length)) {
        return
      }
      // eslint-disable-next-line
      this.myChart = echarts.init(this.$refs.chart)
      let option
      let series = []
      let legend = Array.isArray(this.legend) ? this.legend : [this.legend]
      let seriesData = this.series
      Array.isArray(this.series[0]) || (seriesData = [seriesData])

      seriesData.forEach((data, index) => {
        let obj = {
          name: legend[index],
          type: 'line',
          smooth: true,
          symbol: 'circle', // 数值点类型
          symbolSize: 6, // 数值点大小
          emphasis: {
            focus: 'series',
          },
          data,
        }
        series.push(obj)
      })

      option = {
        color: ['#3A8FFF', '#FFA800', '#FF3A3A', '#00C48D'],
        // title: {
        //   text: 'Stacked Area Chart'
        // },
        tooltip: {
          trigger: 'axis',
          // textStyle: {
          //   color: '#FFF', // 设置文字颜色
          //   fontSize: 12,
          // },
          // backgroundColor: '#505050',
          // borderWidth: 0,
          // axisPointer: {
          //   type: 'cross',
          //   label: {
          //     backgroundColor: '#6a7985',
          //   },
          // },
        },
        legend: {
          data: legend,
          // lineStyle: {
          //   width: 0
          // },
          // itemStyle: {
          //   borderWidth: 10,
          //   decal: {
          //     symbol: 'rect'
          //   }
          // }
          x: 'right',
          y: 'top',
          // selectedMode: false,
          type: 'scroll', // 分页类型
          icon: 'roundRect',
          itemWidth: 24,
          itemHeight: 4,
          itemGap: 14,
          tooltip: {
            show: true,
          },
          textStyle: {
            color: '#36363A',
            fontSize: 12,
            lineHeight: 14,
          },
        },
        grid: {
          left: '3%',
          right: '3%',
          bottom: '20px',
          top: '40px',
          containLabel: true,
        },
        xAxis: [
          {
            type: 'category',
            data: this.xData,
            // boundaryGap: false,
            offset: 5,
            axisLine: {
              lineStyle: {
                color: '#ccc',
              },
            },
            axisLabel: {
              // 坐标轴刻度标签的相关设置。
              // interval: 0,
              color: 'rgba(153, 153, 153, 1)', // 坐标字体颜色
            },
            axisTick: {
              // 横坐标刻度
              alignWithLabel: true,
              show: true,
            },
          },
        ],
        yAxis: [
          {
            type: 'value',
            axisLine: {
              lineStyle: {
                color: '#ccc',
              },
              show: false,
            },
            axisLabel: {
              // 坐标轴刻度标签的相关设置。
              // interval: 0,
              color: 'rgba(153, 153, 153, 1)', // 坐标字体颜色
            },
          },
        ],
        series,
      }
      this.option && merge(option, this.option)
      option && this.myChart.setOption(option)

      new ResizeObserver((entries) => {
        this.myChart.resize()
      }).observe(this.$refs.chart)
    },
  },
}
</script>

<template>
  <div v-if="xData?.length && series?.length" ref="chart" class="chart-line chart" key="1"></div>
  <div v-else class="chart-line chart" key="2">
    <div class="cc"><elEmpty /></div>
  </div>
</template>

<style lang="scss" scoped>
.chart {
  position: relative;
  max-height: 100%;
  min-width: 400px;
  min-height: 400px;
}
</style>
