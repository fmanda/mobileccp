<template>
  <div class="app-container">
    <el-card class="box-card">
      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in data"
          :key="index"
          :timestamp="item.upload_date"
        >
          {{ item.content }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script>
import { getUploadLog } from '@/api/kpidept'

export default {
  data() {
    return {
      data: [],
      listLoading: true,
      search: ''
    }
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true
      getUploadLog().then(response => {
        this.data = response.data;
        // console.log(this.data);

        for (var item of this.data) {
          item.content = '[User] Admin uploaded file : ' + item.filename;
        }

        this.listLoading = false
      })
    }

  }
}
</script>
