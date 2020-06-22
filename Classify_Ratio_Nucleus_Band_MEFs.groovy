/** 
 * Script developed with Pete Tennant to pick up cells with GFP positive nuclei while excluding cells positive for GFP in nucleus and cytoplasm, or fully negative for GFP as part of the FIVER reporter system
 */

setImageType('FLUORESCENCE');
clearAllObjects();

createSelectAllObject(true);
// run watershed plugin - optimised for MEFs
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImage": 2,  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 45.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 3.0,  "minAreaMicrons": 70.0,  "maxAreaMicrons": 1000.0,  "threshold": 200.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 1.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');

// for each cell, create a new measurement called Ratio which is the ratio of the GFP mean intensity of the nucleus to the cytoplasm GFP mean intensity
detections = getDetectionObjects()

detections.each{
    ratio = measurement(it, "Nucleus: GFP  Quad mean")/ measurement(it, "Cytoplasm: GFP  Quad mean")
    it.getMeasurementList().putMeasurement("Ratio", ratio)
    }

// Classify cells as positive if their ratio is above 1.275
setCellIntensityClassifications("Ratio",1.275)

// Change positive colour from red to magenta
positiveColour = getColorRGB(255,0,255)
getPathClass("Positive").setColor(positiveColour)

// Change negative colour from blue to cyan
negativeColour = getColorRGB(0,255,255)
getPathClass("Negative").setColor(negativeColour)

fireHierarchyUpdate()

// Export Results
def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'annotation results')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)
print 'Results exported to ' + path
