package com.bitalino.opensignals.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;
import com.androidplot.xy.XYStepMode;
import com.bitalino.opensignals.R;
import com.bitalino.opensignals.model.Port;

import java.text.DecimalFormat;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link PortPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PortPageFragment extends RoboFragment {
  private static final String TAG = PortPageFragment.class.getSimpleName();
  private static final String ARG_PORT = "port";

  // The port entity associated with this fragment
  private Port port;

  private XYSeries serie;

  @InjectView(R.id.label_port_description)
  private TextView labelPortDescription;

  @InjectView(R.id.dynamicPlot)
  private XYPlot plotReadings;

  public PortPageFragment(Port port, XYSeries serie) {
    this.port = port;
    this.serie = serie;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_port_page, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    labelPortDescription.setText(getString(R.string.label_port_description, port.getName(), port.getUnit()));

    LineAndPointFormatter format = new LineAndPointFormatter();
    format.setPointLabelFormatter(new PointLabelFormatter());
    plotReadings.addSeries(serie, format);

    setupGraphWidget();
    setupDomainWidget();
    setupRangeWidget();
  }

  private void setupGraphWidget() {
    // Customize the graphwidget
    // Remove the border lines
    plotReadings.setBorderStyle(Plot.BorderStyle.NONE, null, null);
    XYGraphWidget graphWidget = plotReadings.getGraphWidget();

    // Some tune ups tp the padding
    graphWidget.setPaddingTop(45.0f);
    graphWidget.setPaddingLeft(60.0f);
    graphWidget.setPaddingBottom(45.0f);
    graphWidget.setPaddingRight(20.0f);

    // Set transparent bagrounds
    graphWidget.getBackgroundPaint().setColor(Color.TRANSPARENT);
    graphWidget.getGridBackgroundPaint().setColor(Color.TRANSPARENT);

    // Customize the origin domain label, set the applications color, fix up the sizes and tweaks to the gridlines
    graphWidget.getDomainOriginLabelPaint().setColor(getResources().getColor(R.color.opensignals_text_field));
    graphWidget.getDomainOriginLabelPaint().setTextSize(40.0f);
    graphWidget.getDomainOriginLinePaint().setColor(getResources().getColor(R.color.opensignals_text_field));
    graphWidget.getDomainOriginLinePaint().setStrokeWidth(1.5f);
    graphWidget.getDomainGridLinePaint().setColor(Color.TRANSPARENT);

    // Customize the origin range label, set the applications color, fix up the sizes and tweaks to the gridlines
    graphWidget.getRangeOriginLabelPaint().setColor(getResources().getColor(R.color.opensignals_text_field));
    graphWidget.getRangeOriginLabelPaint().setTextSize(40.0f);
    graphWidget.getRangeOriginLinePaint().setColor(getResources().getColor(R.color.opensignals_text_field));
    graphWidget.getRangeOriginLinePaint().setStrokeWidth(1.5f);
    graphWidget.getRangeGridLinePaint().setStrokeWidth(1.0f);

    // Remove unnecessary widgets
    plotReadings.getLayoutManager().remove(plotReadings.getLegendWidget());
    plotReadings.getLayoutManager().remove(plotReadings.getTitleWidget());
  }

  private void setupDomainWidget() {
    // Set up the domain label text, color, size, position, boundaries and step
    plotReadings.setDomainLabel(getString(R.string.label_domain));
    plotReadings.getDomainLabelWidget().getLabelPaint().setColor(getResources().getColor(R.color.opensignals_text_field));
    plotReadings.getDomainLabelWidget().getLabelPaint().setTextSize(40.0f);
    plotReadings.getDomainLabelWidget().setWidth(1.0f, SizeLayoutType.RELATIVE);
    plotReadings.getDomainLabelWidget().getPositionMetrics().getXPositionMetric().set(0, XLayoutStyle.RELATIVE_TO_LEFT);
    plotReadings.getDomainLabelWidget().setClippingEnabled(false);
    plotReadings.setDomainBoundaries(0, 90, BoundaryMode.FIXED);
    plotReadings.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 10);
    plotReadings.setDomainStepValue(10);
    plotReadings.setDomainValueFormat(new DecimalFormat("0"));
    plotReadings.getGraphWidget().getDomainLabelPaint().setColor(getResources().getColor(R.color.opensignals_text_field));
    plotReadings.getGraphWidget().getDomainLabelPaint().setTextSize(40.0f);
  }

  private void setupRangeWidget() {
    // Set up the range label size, position, boundaries and step
    // All according to the the type of port specified
    switch (port.getPort()) {
      case ECG:
        plotReadings.setRangeBoundaries(-2f, 2f, BoundaryMode.FIXED);
        plotReadings.setRangeValueFormat(new DecimalFormat("0.00"));
        plotReadings.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.5);
        plotReadings.setRangeStepValue(0.5);
        break;
      case EMG:
        plotReadings.setRangeBoundaries(-2.0f, 2.0f, BoundaryMode.FIXED);
        plotReadings.setRangeValueFormat(new DecimalFormat("0.00"));
        plotReadings.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.5);
        plotReadings.setRangeStepValue(0.5);
        break;
      case EDA:
        plotReadings.setRangeBoundaries(0, 1000, BoundaryMode.FIXED);
        plotReadings.setRangeValueFormat(new DecimalFormat("0"));
        plotReadings.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 100);
        plotReadings.setRangeStepValue(100);
        break;
      case LUX:
        plotReadings.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        plotReadings.setRangeValueFormat(new DecimalFormat("0"));
        plotReadings.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 10);
        plotReadings.setRangeStepValue(10);
        break;
      case ACC:
        plotReadings.setRangeBoundaries(-5, 5, BoundaryMode.FIXED);
        plotReadings.setRangeValueFormat(new DecimalFormat("0.00"));
        plotReadings.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plotReadings.setRangeStepValue(1);
        break;
    }

    plotReadings.getRangeLabelWidget().setVisible(false);
    plotReadings.getGraphWidget().getRangeLabelPaint().setColor(getResources().getColor(R.color.opensignals_text_field));
    plotReadings.getGraphWidget().getRangeLabelPaint().setTextSize(40.0f);

    plotReadings.getGraphWidget().setRangeLabelHorizontalOffset(10.0f);
  }
}
