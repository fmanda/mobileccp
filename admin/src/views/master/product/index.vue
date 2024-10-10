<template>
  <div class="app-container">

    <el-menu
      :default-active="activeMenu"
      class="el-menu-demo"
      mode="horizontal"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      @select="handleSelect"
      style = "margin-bottom: 10px"
    >
      <!-- <el-menu-item index="1"><i class="el-icon-user"></i>Akan Berlangsung</el-menu-item> -->
      <el-menu-item index="1"><i class="el-icon-date"></i>Browse Product</el-menu-item>
      
    </el-menu>

    <el-input
        placeholder="Pencarian"
        prefix-icon="el-icon-search"
        style = "width: 400px; float: right; margin-top: -60px; margin-right: 10px;"
        @change="handleChangeInputSearch"
        clearable
        v-model="filtertxt">
      </el-input>
    <el-table

      :v-loading="listLoading"
      :data="data.filter(data => !search || data.nama.toLowerCase().includes(search.toLowerCase()))"
      style="width: 100%"
    >
      <!-- <el-table-column type="expand">
        <template slot-scope="props">
          <el-descriptions class="margin-top" style="margin-left: 50px;" title="" :column="2" border>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-mobile-phone"></i>
                sellprice_1
              </template>
              {{props.row.sellprice_1}}
            </el-descriptions-item>
        
          </el-descriptions>
        </template>
      </el-table-column>   -->
      <el-table-column label="SKU" prop="sku" width="120px" />
      <el-table-column label="Nama Barang" prop="nama" width="400px"/>
      <el-table-column label="Principal" prop="principal" />
      <el-table-column label="Merk" prop="merk" />
      <el-table-column label="Uom" prop="uom_1" />
      <el-table-column label="Last Update" prop="last_updated" />
      <el-table-column align="right"  label="Harga" prop="sellprice_1" />
      <el-table-column align="right" width="100px">
        <!-- <template slot="header" slot-scope="scope">
          <el-input
            v-model="search"
            size="mini"
            placeholder="Type to search"
          />
          <span hidden>{{ scope.row }}</span>
        </template> -->
      
          
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="handleEdit(scope.$index, scope.row)"
          >View</el-button>
        </template>
      </el-table-column>
    </el-table>
    <br>
    <!-- <el-button type="success" icon="el-icon-plus" @click.native.prevent="handleNew()">Add Product</el-button> -->

    <el-dialog :title="dialogData.caption" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="dialogData" label-width="120px">
        <el-form-item label="SKU">
          <el-input v-model="dialogData.sku" />
        </el-form-item>
        <el-form-item label="Nama Barang">
          <el-input v-model="dialogData.nama" />
        </el-form-item>
        <el-form-item label="Principal">
          <el-input v-model="dialogData.principal" />
        </el-form-item>
        <el-form-item label="Merk">
          <el-input v-model="dialogData.merk" />
        </el-form-item>
        <el-form-item label="Harga CTN">
          <el-input v-model="dialogData.sellprice_1" />
        </el-form-item>
        <el-form-item label="Harga PCS">
          <el-input v-model="dialogData.sellprice_3" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <!-- <el-button type="primary" @click.native.prevent="saveData()">Update</el-button> -->
        <el-button @click="dialogVisible = false">Close</el-button>
      </span>
    </el-dialog> 

  </div>
</template>

<script>
import { getProduct, getListProduct } from '@/api/product'
import { getProjectCode } from '@/utils/auth';
import { formatCurrency } from '@/utils/index.js'


export default {
  data() {
    return {
      data: [],
      listLoading: true,
      search: '',
      dialogData: {
        caption: ''
      },
      dialogVisible: false,
      activeMenu: '1',
      filtertxt: '',
      projectcode: ''
    }
  },
  created() {
    this.fetchData();
    this.projectcode = getProjectCode()
  },
  methods: {
    fetchData() {
      this.listLoading = true
      getListProduct(this.filtertxt).then(response => {
        this.data = response.data;
        this.listLoading = false

        this.data = this.data.map(item => {
          return {
            ...item, // Keep all other properties intact
            sellprice_1	: formatCurrency(item.sellprice_1),
            sellprice_2	: formatCurrency(item.sellprice_2),
            sellprice_3	: formatCurrency(item.sellprice_3)
          };
        });

      })
    },
    showDialog(id) {
      // console.log('test');
      getProduct(id).then(response => {
        this.dialogData = response.data;
        if (id === 0) {
          this.dialogData = { caption: '' }
        } else {
          this.dialogData.caption = 'Detail Product';
        }

        this.dialogVisible = true;
      })
    },
    handleEdit(index, row) {
      this.showDialog(row.sku);
      
      
      // this.$router.push({ name: 'update_department', params: { id: row.id }})
    },
    handleNew() {
      this.showDialog(0);
      // this.$router.push({ path: '/master/update_department' })
    },
    handleSelect(key, keyPath) {
      // console.log(key, keyPath);
      this.activeMenu  = key;
      this.fetchData()
    },
    handleChangeInputSearch(){
      // console.log('handleChangeInputSearch');
      this.fetchData()
    },
    saveData() {
      this.$message({
        type: 'warning',
        message: "Don't bother trying, Read Only"
      });
      // var vm = this;
      // postDepartment(this.dialogData).then(response => {
      //   vm.$message({
      //     type: 'success',
      //     message: 'Data Berhasil Disimpan'
      //   });
      //   vm.dialogVisible = false;
      //   vm.fetchData();
      // })
    }
  }
}
</script>

<style scoped>
  .el-table >>> .cell {
    word-break: break-word;
    white-space: pre-wrap;
  }

  .el-table >>> thead {
    color: rgb(191, 203, 217);
    font-weight: 500;
    background: #304156;
  }

  .el-table >>> th {
    /* color: rgb(191, 203, 217); */
    background: #304156;
  }
</style>
