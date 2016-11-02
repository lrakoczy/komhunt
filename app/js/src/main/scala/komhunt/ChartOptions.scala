package komhunt

import com.highcharts.HighchartsAliases._
import com.highcharts.HighchartsUtils._
import com.highcharts.config.{HighchartsConfig, _}

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.{Date, Object, UndefOr, |}

@ScalaJSDefined
class ChartConfiguration(segment: Segment, predictionData: List[PredictionData]) extends HighchartsConfig {
  override val chart: Cfg[Chart] = new Chart {
  }

  override val title: Cfg[Title] = new Title {
    override val text: UndefOr[String] = segment.name
  }

  override val xAxis: Cfg[XAxis] = new XAxis {
    override val categories: CategoriesCfg = js.Array(predictionData.map(dp => {
      val d = new Date(dp.time * 1000L)
      f"${d.getMonth()}%02d.${d.getDay()}%02d ${d.getHours()}%02d:${d.getMinutes()}%02d"
    }): _*)
  }

  override val yAxis: Cfg[YAxis] = new YAxis {
    override val id:js.UndefOr[String] = "speed"
    override val title: Cfg[YAxisTitle] = new YAxisTitle {
      override val text: UndefOr[String] = "Wind speed (km/h)"
    }
  }

  override val series: SeriesCfg = js.Array[AnySeries](
    new SeriesColumn {
      override val yAxis: js.UndefOr[Double | String] = "speed"
      override val showInLegend: js.UndefOr[Boolean] = false
      override val name: UndefOr[String] = "Wind speed"
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
        }): _*)
    }
  )
}