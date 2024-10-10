<template>

 
  <div class="dashboard-container">

    <el-menu
      :default-active="activeMenu"
      class="el-menu-demo"
      mode="horizontal"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      style = "margin-bottom: 10px"
    >
      <!-- <el-menu-item index="1"><i class="el-icon-user"></i>Akan Berlangsung</el-menu-item> -->
      <el-menu-item index="1"><i class="el-icon-date"></i>Mobility Cabang {{ projectName }}</el-menu-item>
      
    </el-menu>
    
    <el-row>

      <el-col :span="12" class="card-container">
        <el-card class="box-card">
          <!-- <el-badge :value="12" class="item"> -->
            <el-tag type="success">Sales By Area</el-tag>
          <!-- </el-badge> -->

          <DxPieChart
            id="pie"
            :data-source="dataSalesKecamatan"
            type="doughnut"
            title=""
            palette="Soft Pastel"
            class="dashboard-chart"
          >
            <DxSize
              :height="250"
            />
            <DxSeries argument-field="kecamatan" value-field="amount">
              <DxLabel:visible="true">
                <DxConnector :visible="true"/>
              </DxLabel>
            </DxSeries>
            <!-- <DxExport :enabled="true"/> -->
            <DxLegend
              :margin="0"
              horizontal-alignment="right"
              vertical-alignment="top"
            />
            <DxTooltip
              :enabled="true"
              :customize-tooltip="customizeTooltip"
            >
              <DxFormat/>
            </DxTooltip>
          </DxPieChart>  
        </el-card>
      </el-col>

      
      <el-col :span="12" class="card-container">
        <el-card class="box-card">
          <!-- <el-badge :value="totalVisit" class="item"> -->
            <el-tag type="primary">Sales Order 1 Minggu Terakhir</el-tag>
          <!-- </el-badge> -->
          <DxChart
            :data-source="dataSalesWeek"
            class="dashboard-chart"
          >
            <DxSize
              :height="250"
            />
            <DxSeries
              :bar-padding="0.1"
              type="area"
              argument-field="orderdate"
              value-field="amount"
              name="Amount"
              color="#ffaa66"
            />

            <!-- <DxSeries
              :bar-padding="0.1"
              type="area"
              argument-field="dayname"
              value-field="appointment"
              name="appointment"
              color="#1E90FF"
            />

            <DxSeries
              :bar-padding="0.1"
              type="area"
              argument-field="dayname"
              value-field="document"
              name="document"
              color="#8FBC8F"
            /> -->
            <DxTooltip :enabled="true"/>
            <DxLegend orientation="horizontal" position="outside"/>
          </DxChart>
        </el-card>
      </el-col>


      <!-- <el-col :span="8" class="card-container" >
        <el-card class="box-card">
          <el-badge :value="12" class="item">
            <el-tag type="success">Appointment</el-tag>
          </el-badge>

          </DxChart>
        </el-card>
      </el-col> -->
    </el-row>

    <el-card class="box-card">
      <el-tag type="warning">Sales Order 1 Bulan Terakhir</el-tag>
      <DxChart
        :data-source="dataSalesMonth"
        class="dashboard-chart-month"
      >
        <DxSize
          :height="180"
        />
        <DxSeries
          :bar-padding="0.1"
          type="bar"
          argument-field="orderdate"
          value-field="amount"
          name="Amount"
          color="#ffaa66"
        />
<!-- 
        <DxSeries
          :bar-padding="0.1"
          type="bar"
          argument-field="orderdate"
          value-field="appointment"
          name="appointment"
          color="#1E90FF"
        />

        <DxSeries
          :bar-padding="0.1"
          type="bar"
          argument-field="tanggal"
          value-field="document"
          name="document"
          color="#8FBC8F"
        /> -->

        <DxLegend orientation="vertical" :visible="false"/>
      </DxChart>
    </el-card>

  </div>
</template>

<script>

import { DxChart, DxSeries, DxLegend, DxSize } from 'devextreme-vue/chart';
import DxPieChart, {
  DxTooltip,
  DxFormat,
  DxLabel,
  DxConnector
} from 'devextreme-vue/pie-chart';

import { getSalesOrderWeek, getSalesOrderMonth, getSalesOrderKecamatan } from '@/api/dashboard'
import { getProjectName } from '@/utils/auth'

export default {
  name: 'dashboard',
  components: {
    DxChart,
    DxSeries,
    DxPieChart,
    DxFormat,
    DxLabel,
    DxConnector,
    DxTooltip,
    DxLegend,
    DxSize
  },
  data() {
    return {
      dataSalesWeek: [],
      dataSalesMonth: [],
      dataSalesKecamatan: [],
      totalVisit: 0,
      activeMenu: '1',
      projectName: ''
    }
  },
  beforeMount() {
    this.fetchData();
    this.projectName = getProjectName()
  },
  methods: {
    fetchData() {
      getSalesOrderWeek().then(response => {
        var resp = response.data;
        this.dataSalesWeek = resp.map(resp => ({
          ...resp, 
          amount: Number(resp.amount)
        }));
      });

      getSalesOrderMonth().then(response => {
        var resp = response.data;
        this.dataSalesMonth = resp.map(resp => ({
          ...resp, 
          amount: Number(resp.amount)
        }));
      });

      getSalesOrderKecamatan().then(response => {
        var resp = response.data;
        var resp =  resp.map(resp => ({
          ...resp, 
          amount: Number(resp.amount),
        }));
        this.dataSalesKecamatan = resp;
      });
    },
    customizeTooltip({ valueText, percent }) {
      return {
        text: `${valueText} - ${(percent * 100).toFixed(2)}%`,
      };
    },
  }

}

</script>

<style lang="scss" scoped>
  .dashboard {
    &-container {
      margin: 10px;
    }
    &-text {
      font-size: 30px;
      line-height: 46px;
    }
  }

  .box-card {
    width: 100%;
  }

  .card-container {
    padding: 5px;
  }

  .dashboard-chart {
    height: 250px;
  }

  .dashboard-chart-month {
    height: 180px;
  }
</style>
