# MAT-SG
Multiple Aspect Trajectory Summarization based on a Spatial Grid


# Overview
MAT-SG is a grid-based method for summarization of multiple aspect trajectories (MAT). The method is divided in two internal components: 
- (i) spatial segmentation: Responsible for segmenting the trajectories space into a grid of cells by clustering the data points into the same cell. 
- (ii) data summarization: Generates the representative MAT and computes a representative point (pr) for each relevant cell summarizing each dimension.
	A representative point is determined by the summarization of spatial, temporal and semantic data of the cell.

# Installation:
The repository is configured as a NetBeans project. It should be installed as follow:
- Download the repository
- Open the folder as a project in NetBeans
- Add a new folder called datasets and insert the datasets in it

# How to run:
The configuration to create the summarization of a specific dataset is found in the file “TestTrajectoryCellSize”. The following parameters must be configured by the analyst:
- File path: Divided between the variables dir that receives the directory of the file, filename and extension which must be .csv.
- z: Determines the size of the spatial grid as z times the average dispersion of points.
- rc: Determines how much percentage a cell must represent of the total of points to be considered in the summarization. Must be a value between 0 and 1. Default value = 1%
- Trv: Rate of representativeness value for the temporal ranking of rt. Default value = 10%

# Authors:
The authors of this project organization are:

- Vanessa Lago Machado
- Ronaldo dos Santos Mello (advisor)
- Vânia Bogorny (co-advisor)
- Tiago Oliveira da Luz (review and documentation)
