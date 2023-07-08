#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include "plot.h"
#include <QMainWindow>
#include <QtDataVisualization>
#include <QtWidgets>

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = 0);
    ~MainWindow();

private:
    QtDataVisualization::Q3DSurface *m_graph;

    QSlider *m_axisMinSliderX;
    QSlider *m_axisMaxSliderX;
    QSlider *m_axisMinSliderZ;
    QSlider *m_axisMaxSliderZ;

    QLineEdit *m_columnMin;
    QLineEdit *m_columnMax;
    QLineEdit *m_rowMin;
    QLineEdit *m_rowMax;
    QLineEdit *m_lineX;
    QLineEdit *m_lineZ;

    //Groups
    QGroupBox *m_axisGroupBox;
    QGroupBox *m_modelGroupBox;
    QGroupBox *m_selectionGroupBox;
    QGroupBox *m_stepGroupBox;
    QGroupBox *m_colorGroupBox;
    QGroupBox *m_columnGroupBox;
    QGroupBox *m_rowGroupBox;
    QGroupBox *m_langGroupBox;

    QAction *m_actSave, *m_actLoad;

    QRadioButton *m_modeNoneRB, *m_modeItemRB;

    QPushButton *m_recalc;

    QCheckBox *m_showGrid;
    QCheckBox *m_showLabel;
    QCheckBox *m_showLabelBorder;

    QComboBox *m_lang;

    QMenu *m_menu;
    QToolBar *m_tool;

    QTranslator *m_languageTranslator;

    QLabel *m_labelStepX;
    QLabel *m_labelStepZ;

    Plot *m_Sin;

    int m_systemLang = 0;

    //Slots
    void enableSincFirst(bool enable);
    void enableSincSecond(bool enable);

    void showGrid(bool enable);
    void showLabel(bool enable);
    void showLabelBorder(bool enable);
    void changeStatusBar(const QString &label);

    void changeStep();

    void toggleModeNone()
    {
        m_graph->setSelectionMode(QtDataVisualization::QAbstract3DGraph::SelectionNone);
    }
    void toggleModeItem()
    {
        m_graph->setSelectionMode(QtDataVisualization::QAbstract3DGraph::SelectionItem);
    }

    void setFirstGradient();
    void setSecondGradient();

    void setAxisMinSliderX(QSlider *slider) { m_axisMinSliderX = slider; }
    void setAxisMaxSliderX(QSlider *slider) { m_axisMaxSliderX = slider; }
    void setAxisMinSliderZ(QSlider *slider) { m_axisMinSliderZ = slider; }
    void setAxisMaxSliderZ(QSlider *slider) { m_axisMaxSliderZ = slider; }

    void editMinX(const QString &text);
    void editMaxX(const QString &text);
    void editMinZ(const QString &text);
    void editMaxZ(const QString &text);

    void adjustXMin(int min);
    void adjustXMax(int max);
    void adjustZMin(int min);
    void adjustZMax(int max);

    void doSave();
    void loadSettings();

    void changeLang(const QString &label);

    void changeEvent(QEvent *event);
    //

    void setAxisXRange(float min, float max);
    void setAxisZRange(float min, float max);

    void recalcSlider();
};

#endif // MAINWINDOW_H
