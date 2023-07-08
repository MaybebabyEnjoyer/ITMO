#include "mainwindow.h"
#include "plot.h"
#include <QMainWindow>
#include <QtGui/QPainter>
#include <QtGui/QScreen>
#include <QtWidgets>

using namespace QtDataVisualization;

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    m_languageTranslator = new QTranslator(this);

    m_Sin = new Plot();
    m_graph = new Q3DSurface();
    m_graph->axisX()->setLabelFormat("%.3f");
    m_graph->axisZ()->setLabelFormat("%.3f");
    m_graph->setAxisX(new QValue3DAxis);
    m_graph->setAxisY(new QValue3DAxis);
    m_graph->setAxisZ(new QValue3DAxis);

    QWidget *container = QWidget::createWindowContainer(m_graph);

    QSize screenSize = m_graph->screen()->size();
    container->setMinimumSize(QSize(screenSize.width() / 2, screenSize.height() / 2));
    container->setMaximumSize(screenSize);
    container->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
    container->setFocusPolicy(Qt::StrongFocus);

    QWidget *widget = new QWidget;
    QHBoxLayout *hLayout = new QHBoxLayout(widget);
    QVBoxLayout *vLayout = new QVBoxLayout();
    hLayout->addWidget(container, 1);
    hLayout->addLayout(vLayout);
    vLayout->setAlignment(Qt::AlignTop);

    widget->setWindowTitle(QStringLiteral("Graph"));

    //Axises settings

    m_axisGroupBox = new QGroupBox(tr("Axises settings"));

    m_showGrid = new QCheckBox(widget);
    m_showGrid->setText(tr("show grid"));
    m_showGrid->setChecked(true);

    m_showLabel = new QCheckBox(widget);
    m_showLabel->setText(tr("show label"));
    m_showLabel->setChecked(true);

    m_graph->activeTheme()->setLabelBorderEnabled(false);
    m_showLabelBorder = new QCheckBox(widget);
    m_showLabelBorder->setText(tr("show label border"));
    m_showLabelBorder->setChecked(false);

    QVBoxLayout *axisVBox = new QVBoxLayout;
    axisVBox->addWidget(m_showGrid);
    axisVBox->addWidget(m_showLabel);
    axisVBox->addWidget(m_showLabelBorder);
    m_axisGroupBox->setLayout(axisVBox);

    QObject::connect(m_showLabelBorder, &QRadioButton::toggled, this, &MainWindow::showLabelBorder);
    QObject::connect(m_showLabel, &QRadioButton::toggled, this, &MainWindow::showLabel);
    QObject::connect(m_showGrid, &QRadioButton::toggled, this, &MainWindow::showGrid);

    //Graph settings

    m_modelGroupBox = new QGroupBox(tr("Graph"));

    QRadioButton *Sinc1ModelRB = new QRadioButton(widget);
    Sinc1ModelRB->setText(QStringLiteral("sinc1"));
    Sinc1ModelRB->setChecked(false);

    QRadioButton *Sinc2ModelRB = new QRadioButton(widget);
    Sinc2ModelRB->setText(QStringLiteral("sinc2"));
    Sinc2ModelRB->setChecked(false);

    QVBoxLayout *modelVBox = new QVBoxLayout;
    modelVBox->addWidget(Sinc1ModelRB);
    modelVBox->addWidget(Sinc2ModelRB);
    m_modelGroupBox->setLayout(modelVBox);

    QObject::connect(Sinc1ModelRB, &QRadioButton::toggled, this, &MainWindow::enableSincFirst);
    QObject::connect(Sinc2ModelRB, &QRadioButton::toggled, this, &MainWindow::enableSincSecond);

    //Status bar
    QObject::connect(m_Sin->m_SincFirstSeries,
                     &QAbstract3DSeries::itemLabelChanged,
                     this,
                     &MainWindow::changeStatusBar);
    QObject::connect(m_Sin->m_SincSecondSeries,
                     &QAbstract3DSeries::itemLabelChanged,
                     this,
                     &MainWindow::changeStatusBar);

    //Selection settings

    m_selectionGroupBox = new QGroupBox(tr("Selection Mode"));

    m_modeNoneRB = new QRadioButton(widget);
    m_modeNoneRB->setText(tr("No selection"));
    m_modeNoneRB->setChecked(false);

    m_modeItemRB = new QRadioButton(widget);
    m_modeItemRB->setText(tr("Item"));
    m_modeItemRB->setChecked(true);

    QVBoxLayout *selectionVBox = new QVBoxLayout;
    selectionVBox->addWidget(m_modeNoneRB);
    selectionVBox->addWidget(m_modeItemRB);
    m_selectionGroupBox->setLayout(selectionVBox);

    QObject::connect(m_modeNoneRB, &QRadioButton::toggled, this, &MainWindow::toggleModeNone);
    QObject::connect(m_modeItemRB, &QRadioButton::toggled, this, &MainWindow::toggleModeItem);

    //Step settings

    m_stepGroupBox = new QGroupBox(tr("Step settings"));

    m_labelStepX = new QLabel(tr("Step X"));

    m_lineX = new QLineEdit(widget);
    m_lineX->setText(QStringLiteral("50"));

    m_labelStepZ = new QLabel(tr("Step Z"));

    m_lineZ = new QLineEdit(widget);
    m_lineZ->setText(QStringLiteral("50"));

    m_recalc = new QPushButton(widget);
    m_recalc->setText(tr("recalc"));

    QVBoxLayout *stepVBox = new QVBoxLayout;
    stepVBox->addWidget(m_labelStepX);
    stepVBox->addWidget(m_lineX);
    stepVBox->addWidget(m_labelStepZ);
    stepVBox->addWidget(m_lineZ);
    stepVBox->addWidget(m_recalc);
    m_stepGroupBox->setLayout(stepVBox);

    QObject::connect(m_recalc, &QPushButton::clicked, this, &MainWindow::changeStep);

    //Column range settings

    m_columnGroupBox = new QGroupBox(tr("Column range"));

    m_axisMinSliderX = new QSlider(Qt::Horizontal, widget);
    m_axisMinSliderX->setMinimum(0);
    m_axisMinSliderX->setTickInterval(1);
    m_axisMinSliderX->setEnabled(true);

    m_axisMaxSliderX = new QSlider(Qt::Horizontal, widget);
    m_axisMaxSliderX->setMinimum(1);
    m_axisMaxSliderX->setTickInterval(1);
    m_axisMaxSliderX->setEnabled(true);

    m_columnMin = new QLineEdit(widget);
    m_columnMin->setText("0");
    m_columnMax = new QLineEdit(widget);
    m_columnMax->setText("0");

    QVBoxLayout *columnVBox = new QVBoxLayout;
    columnVBox->addWidget(m_axisMinSliderX);
    columnVBox->addWidget(m_columnMin);
    columnVBox->addWidget(m_axisMaxSliderX);
    columnVBox->addWidget(m_columnMax);
    m_columnGroupBox->setLayout(columnVBox);

    QObject::connect(m_columnMin, &QLineEdit::textEdited, this, &MainWindow::editMinX);
    QObject::connect(m_columnMax, &QLineEdit::textEdited, this, &MainWindow::editMaxX);
    QObject::connect(m_axisMinSliderX, &QSlider::valueChanged, this, &MainWindow::adjustXMin);
    QObject::connect(m_axisMaxSliderX, &QSlider::valueChanged, this, &MainWindow::adjustXMax);

    //Row range settings

    m_rowGroupBox = new QGroupBox(tr("Row range"));

    m_axisMinSliderZ = new QSlider(Qt::Horizontal, widget);
    m_axisMinSliderZ->setMinimum(0);
    m_axisMinSliderZ->setTickInterval(1);
    m_axisMinSliderZ->setEnabled(true);
    m_axisMaxSliderZ = new QSlider(Qt::Horizontal, widget);
    m_axisMaxSliderZ->setMinimum(1);
    m_axisMaxSliderZ->setTickInterval(1);
    m_axisMaxSliderZ->setEnabled(true);

    m_rowMin = new QLineEdit(widget);
    m_rowMin->setText("0");
    m_rowMax = new QLineEdit(widget);
    m_rowMax->setText("0");

    QVBoxLayout *rowVBox = new QVBoxLayout;
    rowVBox->addWidget(m_axisMinSliderZ);
    rowVBox->addWidget(m_rowMin);
    rowVBox->addWidget(m_axisMaxSliderZ);
    rowVBox->addWidget(m_rowMax);
    m_rowGroupBox->setLayout(rowVBox);

    QObject::connect(m_rowMin, &QLineEdit::textEdited, this, &MainWindow::editMinZ);
    QObject::connect(m_rowMax, &QLineEdit::textEdited, this, &MainWindow::editMaxZ);
    QObject::connect(m_axisMinSliderZ, &QSlider::valueChanged, this, &MainWindow::adjustZMin);
    QObject::connect(m_axisMaxSliderZ, &QSlider::valueChanged, this, &MainWindow::adjustZMax);

    //Gradient settings

    m_colorGroupBox = new QGroupBox(tr("Custom gradient"));

    QLinearGradient gradientFirst(0, 0, 1, 100);
    gradientFirst.setColorAt(1.0, Qt::darkRed);
    gradientFirst.setColorAt(0.67, Qt::magenta);
    gradientFirst.setColorAt(0.33, Qt::blue);
    gradientFirst.setColorAt(0.0, Qt::darkBlue);
    QPixmap pm(24, 100);
    QPainter pmp(&pm);
    pmp.setBrush(QBrush(gradientFirst));
    pmp.setPen(Qt::NoPen);
    pmp.drawRect(0, 0, 24, 100);
    QPushButton *gradientFirstPB = new QPushButton(widget);
    gradientFirstPB->setIcon(QIcon(pm));
    gradientFirstPB->setIconSize(QSize(24, 100));

    QLinearGradient gradientSecond(0, 0, 1, 100);
    gradientSecond.setColorAt(1.0, Qt::darkGreen);
    gradientSecond.setColorAt(0.5, Qt::yellow);
    gradientSecond.setColorAt(0.2, Qt::red);
    gradientSecond.setColorAt(0.0, Qt::darkRed);
    pmp.setBrush(QBrush(gradientSecond));
    pmp.drawRect(0, 0, 24, 100);
    QPushButton *gradientSecondPB = new QPushButton(widget);
    gradientSecondPB->setIcon(QIcon(pm));
    gradientSecondPB->setIconSize(QSize(24, 100));

    QHBoxLayout *colorHBox = new QHBoxLayout;
    colorHBox->addWidget(gradientFirstPB);
    colorHBox->addWidget(gradientSecondPB);
    m_colorGroupBox->setLayout(colorHBox);

    QObject::connect(gradientFirstPB, &QPushButton::pressed, this, &MainWindow::setFirstGradient);
    QObject::connect(gradientSecondPB, &QPushButton::pressed, this, &MainWindow::setSecondGradient);

    //Language settings

    m_langGroupBox = new QGroupBox(tr("Language settings"));

    m_lang = new QComboBox(widget);
    m_lang->addItem("English");
    m_lang->addItem("Русский");

    QVBoxLayout *langVBox = new QVBoxLayout;
    langVBox->addWidget(m_lang);
    m_langGroupBox->setLayout(langVBox);

    QObject::connect(m_lang, &QComboBox::currentTextChanged, this, &MainWindow::changeLang);

    //Settings
    QCoreApplication::setOrganizationName("./");
    QSettings::setDefaultFormat(QSettings::IniFormat);
    QSettings::setPath(QSettings::IniFormat,
                       QSettings::UserScope,
                       QCoreApplication::applicationDirPath());

    m_actSave = new QAction(style()->standardIcon(QStyle::SP_DialogSaveButton), tr("&Save"), this);
    m_actSave->setShortcuts(QKeySequence::Save);
    m_actSave->setStatusTip(tr("Save settings"));
    connect(m_actSave, &QAction::triggered, this, &MainWindow::doSave);

    m_actLoad = new QAction(style()->standardIcon(QStyle::SP_BrowserReload), tr("&Load"), this);
    m_actLoad->setShortcuts(QKeySequence::Refresh);
    m_actLoad->setStatusTip(tr("Load settings"));
    connect(m_actLoad, &QAction::triggered, this, &MainWindow::loadSettings);

    m_menu = menuBar()->addMenu(tr("&Settings"));
    m_menu->addAction(m_actSave);
    m_menu->addAction(m_actLoad);

    m_tool = addToolBar(tr("Toolbar"));
    m_tool->addAction(m_actSave);
    m_tool->addAction(m_actLoad);

    vLayout->addWidget(m_langGroupBox);
    vLayout->addWidget(m_axisGroupBox);
    vLayout->addWidget(m_modelGroupBox);
    vLayout->addWidget(m_selectionGroupBox);
    vLayout->addWidget(m_stepGroupBox);
    vLayout->addWidget(m_columnGroupBox);
    vLayout->addWidget(m_rowGroupBox);
    vLayout->addWidget(m_colorGroupBox);

    Sinc1ModelRB->setChecked(true);

    QLocale *locale = new QLocale();
    locale->system();

    if (QLocale::languageToString(locale->language()) == "Russian") {
        m_systemLang = 1;
    }

    loadSettings();

    this->setCentralWidget(widget);
}

MainWindow::~MainWindow()
{
    delete m_graph;
}

void MainWindow::doSave()
{
    QSettings settings;
    settings.beginGroup("Settings");
    settings.setValue("language", m_lang->currentIndex());
    settings.setValue("m_sampleCountX", m_Sin->m_sampleCountX);
    settings.setValue("m_sampleCountZ", m_Sin->m_sampleCountZ);
    settings.setValue("m_showLabel", m_showLabel->isChecked());
    settings.setValue("m_showLabelBorder", m_showLabelBorder->isChecked());
    settings.setValue("m_showGrid", m_showGrid->isChecked());
    settings.setValue("m_modeNoneRB", m_modeNoneRB->isEnabled());
    settings.endGroup();
}

void MainWindow::loadSettings()
{
    QSettings settings;
    settings.beginGroup("Settings");
    int temp = settings.value("language", m_systemLang).toInt();
    m_lang->setCurrentIndex(temp);
    m_Sin->m_sampleCountX = settings.value("m_sampleCountX", 50).toInt();
    m_lineX->setText(QString::number(m_Sin->m_sampleCountX));
    m_Sin->m_sampleCountZ = settings.value("m_sampleCountZ", 50).toInt();
    m_lineZ->setText(QString::number(m_Sin->m_sampleCountZ));
    m_showLabel->setChecked(settings.value("m_showLabel", true).toBool());
    m_showLabelBorder->setChecked(settings.value("m_showLabelBorder", false).toBool());
    m_showGrid->setChecked(settings.value("m_showGrid", true).toBool());
    m_modeNoneRB->setChecked(settings.value("m_modeNoneRB", false).toBool());
    settings.endGroup();
    if (m_Sin->m_sampleCountX != 50 || m_Sin->m_sampleCountZ != 50) {
        m_Sin->recalc();
        recalcSlider();
    }
}

void MainWindow::changeLang(const QString &label)
{
    if (label == "Русский") {
        m_languageTranslator->load("./QtLanguage_ru");
        qApp->installTranslator(m_languageTranslator);
    }
    if (label == "English") {
        m_languageTranslator->load("./QtLanguage_en");
        qApp->installTranslator(m_languageTranslator);
    }
}

void MainWindow::changeEvent(QEvent *event)
{
    if (event->type() == QEvent::LanguageChange) {
        m_axisGroupBox->setTitle(tr("Axises settings"));
        m_showGrid->setText(tr("show grid"));
        m_showLabel->setText(tr("show label"));
        m_showLabelBorder->setText(tr("show label border"));
        m_modelGroupBox->setTitle(tr("Graph"));
        m_selectionGroupBox->setTitle(tr("Selection Mode"));
        m_modeNoneRB->setText(tr("No selection"));
        m_modeItemRB->setText(tr("Item"));
        m_stepGroupBox->setTitle(tr("Step settings"));
        m_recalc->setText(tr("recalc"));
        m_colorGroupBox->setTitle(tr("Custom gradient"));
        m_langGroupBox->setTitle(tr("Language settings"));
        m_columnGroupBox->setTitle(tr("Column range"));
        m_rowGroupBox->setTitle(tr("Row range"));
        m_actSave->setText(tr("&Save"));
        m_actSave->setStatusTip(tr("Save settings"));
        m_actLoad->setText(tr("&Load"));
        m_actLoad->setStatusTip(tr("Load settings"));
        m_menu->setTitle(tr("&Settings"));
        m_labelStepX->setText(tr("Step X"));
        m_labelStepZ->setText(tr("Step Z"));
    }
}

void MainWindow::enableSincFirst(bool enable)
{
    if (enable) {
        m_Sin->m_SincFirstSeries->setDrawMode(QSurface3DSeries::DrawSurfaceAndWireframe);
        m_Sin->m_SincFirstSeries->setFlatShadingEnabled(false);

        m_graph->axisX()->setRange(m_Sin->m_sampleMin, m_Sin->m_sampleMax);
        m_graph->axisY()->setRange(-0.22f, 1.0f);
        m_graph->axisZ()->setRange(m_Sin->m_sampleMin, m_Sin->m_sampleMax);
        m_graph->axisX()->setLabelAutoRotation(30);
        m_graph->axisY()->setLabelAutoRotation(90);
        m_graph->axisZ()->setLabelAutoRotation(30);

        m_graph->removeSeries(m_Sin->m_SincSecondSeries);
        statusBar()->showMessage("");
        m_graph->addSeries(m_Sin->m_SincFirstSeries);

        recalcSlider();
    }
}

void MainWindow::enableSincSecond(bool enable)
{
    if (enable) {
        m_Sin->m_SincSecondSeries->setDrawMode(QSurface3DSeries::DrawSurfaceAndWireframe);
        m_Sin->m_SincSecondSeries->setFlatShadingEnabled(false);

        m_graph->axisX()->setRange(m_Sin->m_sampleMin, m_Sin->m_sampleMax);
        m_graph->axisY()->setRange(-0.22f, 1.0f);
        m_graph->axisZ()->setRange(m_Sin->m_sampleMin, m_Sin->m_sampleMax);
        m_graph->axisX()->setLabelAutoRotation(30);
        m_graph->axisY()->setLabelAutoRotation(90);
        m_graph->axisZ()->setLabelAutoRotation(30);

        m_graph->removeSeries(m_Sin->m_SincFirstSeries);
        statusBar()->showMessage("");
        m_graph->addSeries(m_Sin->m_SincSecondSeries);

        recalcSlider();
    }
}

void MainWindow::changeStep()
{
    bool markerX;
    bool markerZ;
    int stepCountX = m_lineX->text().toInt(&markerX);
    int stepCountZ = m_lineZ->text().toInt(&markerZ);
    if (markerX && markerZ) {
        m_Sin->m_sampleCountX = stepCountX;
        m_Sin->m_sampleCountZ = stepCountZ;
        m_Sin->recalc();
        recalcSlider();
    }
}

void MainWindow::changeStatusBar(const QString &label)
{
    if (!(label.isNull() || label.isEmpty())) {
        QStringList temp = label.split(" ");
        statusBar()->showMessage(QString("x = ") + temp[0] + QString(" y = ") + temp[1]
                                 + QString(" z = ") + temp[2]);
    } else {
        statusBar()->showMessage(QString(""));
    }
}

void MainWindow::showGrid(bool enable)
{
    if (enable) {
        m_graph->activeTheme()->setGridEnabled(true);
    } else {
        m_graph->activeTheme()->setGridEnabled(false);
    }
}

void MainWindow::showLabel(bool enable)
{
    if (enable) {
        m_graph->axisX()->setLabelFormat("%.3f");
        m_graph->axisY()->setLabelFormat("%.3f");
        m_graph->axisZ()->setLabelFormat("%.3f");
        m_showLabelBorder->setEnabled(true);
    } else {
        m_graph->axisX()->setLabelFormat("");
        m_graph->axisY()->setLabelFormat("");
        m_graph->axisZ()->setLabelFormat("");
        m_showLabelBorder->setEnabled(false);
        m_showLabelBorder->setChecked(false);
    }
}

void MainWindow::showLabelBorder(bool enable)
{
    if (enable) {
        m_graph->activeTheme()->setLabelBorderEnabled(true);
    } else {
        m_graph->activeTheme()->setLabelBorderEnabled(false);
    }
}

void MainWindow::recalcSlider()
{
    m_Sin->m_rangeMinX = m_Sin->m_sampleMin;
    m_Sin->m_rangeMinZ = m_Sin->m_sampleMin;
    m_Sin->m_stepX = (m_Sin->m_sampleMax - m_Sin->m_sampleMin) / float(m_Sin->m_sampleCountX - 1);
    m_Sin->m_stepZ = (m_Sin->m_sampleMax - m_Sin->m_sampleMin) / float(m_Sin->m_sampleCountZ - 1);
    m_axisMinSliderX->setMaximum(m_Sin->m_sampleCountX - 2);
    m_axisMinSliderX->setValue(0);
    m_columnMin->setText("0");
    m_axisMaxSliderX->setMaximum(m_Sin->m_sampleCountX - 1);
    m_axisMaxSliderX->setValue(m_Sin->m_sampleCountX - 1);
    m_columnMax->setText(QString::number(m_Sin->m_sampleCountX - 1));
    m_axisMinSliderZ->setMaximum(m_Sin->m_sampleCountZ - 2);
    m_axisMinSliderZ->setValue(0);
    m_rowMin->setText("0");
    m_axisMaxSliderZ->setMaximum(m_Sin->m_sampleCountZ - 1);
    m_axisMaxSliderZ->setValue(m_Sin->m_sampleCountZ - 1);
    m_rowMax->setText(QString::number(m_Sin->m_sampleCountZ - 1));
}

void MainWindow::editMinX(const QString &text)
{
    bool mark;
    int min = text.toInt(&mark);
    if (mark) {
        adjustXMin(min);
    }
}

void MainWindow::editMaxX(const QString &text)
{
    bool mark;
    int max = text.toInt(&mark);
    if (mark) {
        adjustXMax(max);
    }
}

void MainWindow::editMinZ(const QString &text)
{
    bool mark;
    int min = text.toInt(&mark);
    if (mark) {
        adjustZMin(min);
    }
}

void MainWindow::editMaxZ(const QString &text)
{
    bool mark;
    int max = text.toInt(&mark);
    if (mark) {
        adjustZMax(max);
    }
}

void MainWindow::adjustXMin(int min)
{
    float minX = m_Sin->m_stepX * float(min) + m_Sin->m_rangeMinX;

    int max = m_axisMaxSliderX->value();
    if (min >= max) {
        max = min + 1;
        m_axisMaxSliderX->setValue(max);
        m_columnMax->setText(QString::number(max));
    }
    float maxX = m_Sin->m_stepX * max + m_Sin->m_rangeMinX;
    m_columnMin->setText(QString::number(min));
    setAxisXRange(minX, maxX);
}

void MainWindow::adjustXMax(int max)
{
    float maxX = m_Sin->m_stepX * float(max) + m_Sin->m_rangeMinX;

    int min = m_axisMinSliderX->value();
    if (max <= min) {
        min = max - 1;
        m_axisMinSliderX->setValue(min);
        m_columnMin->setText(QString::number(min));
    }
    float minX = m_Sin->m_stepX * min + m_Sin->m_rangeMinX;
    m_columnMax->setText(QString::number(max));
    setAxisXRange(minX, maxX);
}

void MainWindow::adjustZMin(int min)
{
    float minZ = m_Sin->m_stepZ * float(min) + m_Sin->m_rangeMinZ;

    int max = m_axisMaxSliderZ->value();
    if (min >= max) {
        max = min + 1;
        m_axisMaxSliderZ->setValue(max);
        m_rowMax->setText(QString::number(max));
    }
    float maxZ = m_Sin->m_stepZ * max + m_Sin->m_rangeMinZ;
    m_rowMin->setText(QString::number(min));
    setAxisZRange(minZ, maxZ);
}

void MainWindow::adjustZMax(int max)
{
    float maxZ = m_Sin->m_stepZ * float(max) + m_Sin->m_rangeMinZ;

    int min = m_axisMinSliderZ->value();
    if (max <= min) {
        min = max - 1;
        m_axisMinSliderZ->setValue(min);
        m_rowMin->setText(QString::number(min));
    }
    float minZ = m_Sin->m_stepZ * min + m_Sin->m_rangeMinZ;
    m_rowMax->setText(QString::number(max));
    setAxisZRange(minZ, maxZ);
}

void MainWindow::setAxisXRange(float min, float max)
{
    m_graph->axisX()->setRange(min, max);
}

void MainWindow::setAxisZRange(float min, float max)
{
    m_graph->axisZ()->setRange(min, max);
}

void MainWindow::setFirstGradient()
{
    QLinearGradient gr;
    gr.setColorAt(0.0, Qt::darkRed);
    gr.setColorAt(0.33, Qt::magenta);
    gr.setColorAt(0.67, Qt::blue);
    gr.setColorAt(1.0, Qt::darkBlue);

    m_graph->seriesList().at(0)->setBaseGradient(gr);
    m_graph->seriesList().at(0)->setColorStyle(Q3DTheme::ColorStyleRangeGradient);
}

void MainWindow::setSecondGradient()
{
    QLinearGradient gr;
    gr.setColorAt(0.0, Qt::darkGreen);
    gr.setColorAt(0.5, Qt::yellow);
    gr.setColorAt(0.8, Qt::red);
    gr.setColorAt(1.0, Qt::darkRed);

    m_graph->seriesList().at(0)->setBaseGradient(gr);
    m_graph->seriesList().at(0)->setColorStyle(Q3DTheme::ColorStyleRangeGradient);
}
