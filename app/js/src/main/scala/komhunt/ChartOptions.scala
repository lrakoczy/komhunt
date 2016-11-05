package komhunt

import com.highcharts.CleanJsObject
import com.highcharts.HighchartsAliases._
import com.highcharts.HighchartsUtils._
import com.highcharts.config.{HighchartsConfig, _}

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Object, UndefOr, |}

@ScalaJSDefined
class ChartConfiguration(segment: Segment, predictionData: List[PredictionData], windRelevance: Double) extends HighchartsConfig {
  override val chart: Cfg[Chart] = new Chart {
    override val zoomType: UndefOr[String] = "x"
  }

  override val title: Cfg[Title] = new Title {
    override val useHTML: UndefOr[Boolean] = true
    override val text: UndefOr[String] = s"""<a target="_blank" href="https://strava.com/segments/${segment.id}" title="Go to Strava segment page">${segment.name} </a>"""
  }

  override val subtitle: Cfg[Subtitle] = new Subtitle {
    val dist: Double = segment.distance / 1000
    override val text: UndefOr[String] = f"Segment distance: $dist%.2f km, wind relevance: $windRelevance%.2f (min 0, max 1)"
  }

  override val tooltip: Cfg[Tooltip] = new Tooltip {
    override val shared: UndefOr[Boolean] = true
  }

  override val xAxis: Cfg[XAxis] = new XAxis {
    override val `type`: UndefOr[String] = "datetime"
    override val labels: UndefOr[CleanJsObject[XAxisLabels]] = new XAxisLabels {}
  }

  override val yAxis: UndefOr[CleanJsObject[YAxis]] =
    js.Array(
      new YAxis {
        val softMax: UndefOr[Double] = 30
        override val id: js.UndefOr[String] = "speed"
        override val title: Cfg[YAxisTitle] = new YAxisTitle {
          override val text: UndefOr[String] = "Wind speed (km/h)"
        }
        override val labels: Cfg[YAxisLabels] = new YAxisLabels {
        }
      },
      new YAxis {
        override val max: UndefOr[Double] = 180
        override val id: js.UndefOr[String] = "correlation"
        override val title: Cfg[YAxisTitle] = new YAxisTitle {
          override val text: UndefOr[String] = "Wind correlation"
        }
        override val labels: Cfg[YAxisLabels] = new YAxisLabels {
          override val enabled: UndefOr[Boolean] = true
        }
        override val opposite: UndefOr[Boolean] = true
      }
    ).asInstanceOf[UndefOr[CleanJsObject[YAxis]]]

  override val series: SeriesCfg = js.Array[AnySeries](
    new SeriesColumn {
      override val yAxis: js.UndefOr[Double | String] = "speed"
      override val showInLegend: js.UndefOr[Boolean] = false
      override val name: UndefOr[String] = "Wind speed"
      override val tooltip: UndefOr[CleanJsObject[SeriesColumnTooltip]] = new SeriesColumnTooltip {
        override val valueSuffix: UndefOr[String] = " km/h"
        override val valueDecimals: UndefOr[Double] = 2
      }
      override val data: SeriesCfgData[SeriesColumnData] = js.Array[SeriesColumnData](predictionData.map(dp =>
        new SeriesColumnData {
          val speed = dp.windSpeed * 1.609344d
          val correlation = dp.correlation
          override val color: UndefOr[|[String, Object]] = {
            if (correlation < 45) "red"
            else if (correlation < 90) "orange"
            else if (correlation < 135) "blue"
            else "green"
          }
          override val y: UndefOr[Double] = speed
          override val x: UndefOr[Double] = dp.time * 1000L
        }): _*)
    },
    new SeriesSpline {
      override val yAxis: js.UndefOr[Double | String] = "correlation"
      override val name: UndefOr[String] = "Wind correlation (°)"
      override val showInLegend: js.UndefOr[Boolean] = false
      override val tooltip: UndefOr[CleanJsObject[SeriesSplineTooltip]] = new SeriesSplineTooltip {
        override val valueSuffix: UndefOr[String] = " °"
        override val valueDecimals: UndefOr[Double] = 0
      }
      override val data: SeriesCfgData[SeriesSplineData] = js.Array[SeriesSplineData](predictionData.map(dp =>
        new SeriesSplineData {
          override val y: UndefOr[Double] = dp.correlation
          override val x: UndefOr[Double] = dp.time * 1000L
        }
      ): _*)
      override val marker: Cfg[SeriesSplineMarker] = new SeriesSplineMarker {
        override val lineWidth: UndefOr[Double] = 2
        override val lineColor: UndefOr[String | js.Object] = defaultColor(3)
        override val fillColor: UndefOr[String | js.Object] = "white"
      }
    }
  )
}